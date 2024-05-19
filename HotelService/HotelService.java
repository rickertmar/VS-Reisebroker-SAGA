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

/**
 * Represents a service for managing hotel bookings and cancellations.
 * It interacts with a message broker to receive booking and cancellation requests,
 * and processes these requests asynchronously.
 */
public class HotelService {
    public String name;
    private Hotel[] hotels;
    private static Random random = new Random();

    // A map to cache CompletableFuture objects for asynchronous processing of messages
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

    /**
     * Receives a message from the message broker and processes it.
     * The method simulates probabilistic behavior for not doing anything or not sending a message.
     * @param requestMessage The message received from the message broker.
     */
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

    /**
     * Processes a received message and generates a response based on the message type.
     * Supports HotelBooking and HotelCancel message types.
     * @param requestMessage The message to process.
     * @return A Message object representing the response to the request.
     */
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

    /**
     * Finds a hotel by its name.
     * @param hotelName The name of the hotel to find.
     * @return The Hotel object if found, null otherwise.
     */
    private Hotel findHotel(String hotelName) {
        for (Hotel hotel : hotels) {
            if (hotel.name.equals(hotelName)) {
                return hotel;
            }
        }
        return null;
    }

    /**
     * Sends a response from the cache based on the transaction ID.
     * If the response is not found in the cache, an InterruptedException is thrown.
     * @param transactionId The transaction ID of the message to send.
     * @throws InterruptedException If the response is not found in the cache.
     */
    private void sendFromCache(String transactionId) throws InterruptedException {
        CompletableFuture<Message> answer = Answers.get(transactionId);
        if (answer != null) {
            answer.thenAccept(MessageBroker::send);
        } else {
            throw new InterruptedException("Response for transaction ID " + transactionId + " not found in cache.");
        }
    }
}