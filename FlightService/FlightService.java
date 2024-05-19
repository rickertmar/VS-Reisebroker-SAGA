package FlightService;

import MessageBroker.MessageBroker;
import MessageBroker.Message;
import MessageBroker.Answer;
import MessageBroker.FlightBooking;
import MessageBroker.FlightCancel;

import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CompletableFuture;

import DataFiller.FillProperties;

/**
 * Represents a service for managing flight bookings and cancellations.
 * It interacts with a message broker to receive booking and cancellation requests,
 * and processes these requests asynchronously.
 */
public class FlightService {
    public String name;
    private Flight[] flights;
    private static Random random = new Random();

    // A map to cache CompletableFuture objects for asynchronous processing of messages
    private ConcurrentHashMap<String, CompletableFuture<Message>> Answers = new ConcurrentHashMap<>();

    public FlightService(String name, Flight[] flights) {
        this.name = name;
        this.flights = flights;
    }

    public String getName() {
        return name;
    }

    public String[] getFlights() {
        String[] flightNames = new String[flights.length];
        for (int i = 0; i < flights.length; i++) {
            flightNames[i] = flights[i].name;
        }
        return flightNames;
    }

    /**
     * Finds a flight by its name.
     * @param flightName The name of the flight to find.
     * @return The Flight object if found, null otherwise.
     */
    private Flight findFlight(String flightName) {
        for (Flight flight : flights) {
            if (flight.name.equals(flightName)) {
                return flight;
            }
        }
        return null;
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
     * Supports FlightBooking and FlightCancel message types.
     * @param requestMessage The message to process.
     * @return A Message object representing the response to the request.
     */
    private Message processMessage(Message requestMessage) {
        String transactionId = requestMessage.getTransactionId();
        switch (requestMessage.getContent().getType()) {
            case "FlightBooking":
                FlightBooking flightBooking = (FlightBooking) requestMessage.getContent();
                Flight flight = findFlight(flightBooking.getFlightNumber());
                boolean success = flight.bookSeats(flightBooking.getNumberOfSeats());
                return new Message(transactionId, this.name, requestMessage.getSender(), new Answer(success));
            case "FlightCancel":
                FlightCancel flightCancel = (FlightCancel) requestMessage.getContent();
                Flight flight1 = findFlight(flightCancel.getFlightNumber());
                flight1.releaseSeats(flightCancel.getNoSeats());
                return new Message(transactionId, this.name, requestMessage.getSender(), new Answer(true));
            default:
                return new Message(transactionId, this.name, requestMessage.getSender(), new Answer(false));
        }
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
            throw new InterruptedException();
        }
    }
}