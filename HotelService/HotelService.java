package HotelService;

import DataFiller.FillProperties;
import MessageBroker.HotelCancel;
import MessageBroker.MessageBroker;
import MessageBroker.Message;
import MessageBroker.Answer;
import MessageBroker.HotelBooking;

import java.util.Random;
import java.util.HashMap;
import java.util.Map;
//CompletableFuture
import java.util.concurrent.CompletableFuture;

public class HotelService {
    public String name;
    private Hotel[] hotels;

    private Map<String, CompletableFuture<Message>> Answers = new HashMap<String, CompletableFuture<Message>>();


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

    public void receiveMessage(Message RequestMessage) {
        //todo random chance to not do anything HERE
        int chanceToNotDoAnything = FillProperties.getChanceToNotDoAnything();
        if (randomNumber.nextInt(100) < chanceToNotDoAnything) {
            return;
        }

        String transactionId = RequestMessage.getTransactionId();
        CompletableFuture<Message> cachedAnswer = Answers.get(transactionId);

        if (cachedAnswer != null) {
            cachedAnswer.thenAccept(MessageBroker::send);
        } else {
            // Process the booking asynchronously
            CompletableFuture<Message> newAnswer = CompletableFuture.supplyAsync(() -> {
                switch (RequestMessage.getContent().getType()) {
                    case "HotelBooking":
                        HotelBooking hotelBooking = (HotelBooking) RequestMessage.getContent();
                        Hotel hotel = findHotel(hotelBooking.getHotelName());
                        boolean success = hotel.bookRooms(hotelBooking.getNumberOfRooms());
                        return new Message(transactionId, this.name, RequestMessage.getSender(), new Answer(success));
                    case "HotelCancel":
                        HotelCancel hotelCancel = (HotelCancel) RequestMessage.getContent();
                        Hotel hotel1 = findHotel(hotelCancel.getHotelName());
                        hotel1.releaseRooms(hotelCancel.getNoRooms());
                        return new Message(transactionId, this.name, RequestMessage.getSender(), new Answer(true));
                    default:
                        //invalid type
                        return new Message(transactionId, this.name, RequestMessage.getSender(), new Answer(false));
                }
            });
            // Cache the new CompletableFuture for future requests
            Answers.put(transactionId, newAnswer);

            // todo random chance to not send the message HERE
            int chanceToNotSendMessage = FillProperties.getChanceToNotSendMessage();
            if (randomNumber.nextInt(100) < chanceToNotSendMessage) {
                return;
            }

            // Once the CompletableFuture completes, send the message
            newAnswer.thenAccept(MessageBroker::send);
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
            answer.thenAccept((Message message) -> MessageBroker.send(message));
        } else {
            throw new InterruptedException();
        }
    }
}

