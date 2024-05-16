package HotelService;

import DataFiller.FillProperties;
import MessageBroker.HotelCancel;
import MessageBroker.MessageBroker;
import MessageBroker.Message;
import MessageBroker.Answer;
import MessageBroker.HotelBooking;

import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CompletableFuture;

public class HotelService {
    public String name;
    private Hotel[] hotels;
    private static Random random = new Random();

    private ConcurrentHashMap<String, CompletableFuture<Message>> Answers = new ConcurrentHashMap<>();
    public HotelService(String name, Hotel[] hotels) {
        this.name = name;
        this.hotels = hotels;
    }

    public String getName() {
        return name;
    }

    public String[] getHotels() {
        String[] hotelNames = new String[hotels.length];
        for (int i = 0; i < hotels.length; i++) {
            hotelNames[i] = hotels[i].name;
        }
        return hotelNames;
    }

    private Random randomNumber = new Random();

    public void receiveMessage(Message requestMessage) {
        int chanceToNotDoAnything = FillProperties.getChanceToNotDoAnything();
        if (random.nextInt(100) < chanceToNotDoAnything) {
            return;
        }

        String transactionId = requestMessage.getTransactionId();
        if (transactionId == null) {
            System.err.println("Error: Transaction ID is null in receiveMessage");
            return;
        }
        CompletableFuture<Message> cachedAnswer = Answers.get(transactionId);

        if (cachedAnswer != null) {
            cachedAnswer.thenAccept(MessageBroker::send);
        } else {
            CompletableFuture<Message> newAnswer = CompletableFuture.supplyAsync(() -> processMessage(requestMessage));
            Answers.put(transactionId, newAnswer);

            int chanceToNotSendMessage = FillProperties.getChanceToNotSendMessage();
            if (random.nextInt(100) < chanceToNotSendMessage) {
                return;
            }

            newAnswer.thenAccept(MessageBroker::send);
        }
    }


    private Message processMessage(Message requestMessage) {
        String transactionId = requestMessage.getTransactionId();
        switch (requestMessage.getContent().getType()) {
            case "HotelBooking":
                HotelBooking hotelBooking = (HotelBooking) requestMessage.getContent();
                Hotel hotel = findHotel(hotelBooking.getHotelName());
                boolean success = hotel.bookRooms(hotelBooking.getNumberOfRooms());
                return new Message(transactionId, this.name, requestMessage.getSender(), new Answer(success));
            case "HotelCancel":
                HotelCancel hotelCancel = (HotelCancel) requestMessage.getContent();
                Hotel hotel1 = findHotel(hotelCancel.getHotelName());
                hotel1.releaseRooms(hotelCancel.getNoRooms());
                return new Message(transactionId, this.name, requestMessage.getSender(), new Answer(true));
            default:
                return new Message(transactionId, this.name, requestMessage.getSender(), new Answer(false));
        }
    }

    private Hotel findHotel(String hotelName) {
        for (Hotel hotel : hotels) {
            if (hotel.name.equals(hotelName)) {
                return hotel;
            }
        }
        return null;
    }

    private void sendFromCache(String transactionId) throws InterruptedException {
        CompletableFuture<Message> answer = Answers.get(transactionId);
        if (answer != null) {
            answer.thenAccept(MessageBroker::send);
        } else {
            throw new InterruptedException();
        }
    }
}
