package HotelService;

import MessageBroker.MessageBroker;
import MessageBroker.Message;
import MessageBroker.Answer;
import MessageBroker.HotelBooking;
import test.BookingRequest;

import java.util.concurrent.BlockingQueue;
import java.util.Random;
import java.util.HashMap;
import java.util.Map;

//CompletableFuture
import java.util.concurrent.CompletableFuture;




public class HotelService {
    public String name;
    private Hotel[] hotels;

    private Map<String, CompletableFuture<Message>> Answers = new HashMap<String, CompletableFuture<Message>>();


    public HotelService(String name, int numHotels) {
        this.name = name;
        hotels = new Hotel[numHotels];
        for (int i = 0; i < hotels.length; i++) {
            hotels[i] = new Hotel();
        }
    }


    public void receiveMessage(Message RequestMessage) {
        //todo random chance to not do anything HERE

        String transactionId = RequestMessage.getTransactionId();
        CompletableFuture<Message> cachedAnswer = Answers.get(transactionId);

        if (cachedAnswer != null) {
            cachedAnswer.thenAccept(MessageBroker::send);
        } else {
            // Process the booking asynchronously
            CompletableFuture<Message> newAnswer = CompletableFuture.supplyAsync(() -> {
                HotelBooking hotelBooking = (HotelBooking) RequestMessage.getContent();
                Hotel hotel = findHotel(hotelBooking.getHotelName());
                boolean success = hotel.bookRooms(hotelBooking.getNumberOfRooms());

                return new Message(transactionId, this.name, RequestMessage.getSender(), new Answer(success));
            });

            // Cache the new CompletableFuture for future requests
            Answers.put(transactionId, newAnswer);

            // todo random chance to not send the message HERE

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


    private void sendFromCache(String transactionId)throws InterruptedException{
        CompletableFuture<Message> answer = Answers.get(transactionId);
        if (answer != null) {
            answer.thenAccept((Message message) -> MessageBroker.send(message));
        }
        else {
            throw new InterruptedException();
        }
    }
}






class Hotel {
    private  Random random = new Random();
    public final String name;
    private int totalBeds;
    private int availableBeds;


    public Hotel() {
        // randomized initial available beds
        this.name = "Hotel " + random.nextInt(1000);
        this.totalBeds = random.nextInt(100,200);
        this.availableBeds = random.nextInt(50,100);
    }


    public synchronized boolean bookRooms(int numRooms) {
        if (availableBeds >= numRooms) {
            availableBeds -= numRooms;
            System.out.println(numRooms + " rooms booked successfully at " + name);
            System.out.println("Available beds: " + availableBeds);
            return true;
        } else {
            System.out.println("Insufficient rooms available at " + name);
            return false;
        }
    }

    public synchronized void releaseRooms(int numRooms) {
        availableBeds += numRooms;
        System.out.println(numRooms + " rooms released at " + name);
    }

    public synchronized int getAvailableBeds() {
        return availableBeds;
    }

    public synchronized int getTotalBeds() {
        return totalBeds;
    }
}

