package MessageBroker;

import HotelService.HotelService;
import FlightService.FlightService;
import Tripbroker.TripBroker;
import MessageBroker.MessageQueue;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * The MessageBroker class is responsible for managing the communication between different services
 * such as HotelService and FlightService. It uses a message queue to handle incoming messages and
 * distributes them to the appropriate service. It also tracks messages waiting for a response and
 * handles timeouts for these messages.
 */
public class MessageBroker {
    static ReentrantLock lock = new ReentrantLock(); // Lock for thread-safe operations on shared resources
    static int nMessagesIn = 0; // Counter for incoming messages
    static int nTimeouts = 0; // Counter for message timeouts
    static int nMessagesOut = 0; // Counter for outgoing messages

    // Ring buffer for messages
    private static MessageQueue messageQueue = new MessageQueue(100);

    // Maps for storing services and messages waiting for a response
    private static ConcurrentHashMap<String, Message> waitingForAnswer = new ConcurrentHashMap<>();
    private static int Timeout_ns = 5000; // Timeout threshold in nanoseconds
    private static ConcurrentHashMap<String, HotelService> HotelAddresses = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<String, FlightService> FlightAddresses = new ConcurrentHashMap<>();

    /**
     * Initializes the message broker by starting worker and daemon threads.
     */
    public static void init() {
        for (int i = 0; i < 10; i++) {
            Worker worker = new Worker();
            worker.start();
        }
        Daemon daemon = new Daemon();
        daemon.start();
    }

    /**
     * Registers a HotelService with the message broker.
     *
     * @param name         The name of the hotel service.
     * @param hotelService The HotelService instance.
     */
    public static void registerHotelService(String name, HotelService hotelService) {
        HotelAddresses.put(name, hotelService);
    }

    /**
     * Registers a FlightService with the message broker.
     *
     * @param name          The name of the flight service.
     * @param flightService The FlightService instance.
     */
    public static void registerFlightService(String name, FlightService flightService) {
        FlightAddresses.put(name, flightService);
    }

    /**
     * Sends a message to the appropriate service or adds it to the message queue.
     *
     * @param message The message to be sent.
     */
    public static void send(Message message) {
        try {
            messageQueuePut(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Helper methods for adding to and taking from the message queue
    private static void messageQueuePut(Message message) throws InterruptedException {
        messageQueue.messageQueuePut(message);
    }

    private static Message messageQueueTake() throws InterruptedException {
        return messageQueue.messageQueueTake();
    }

    /**
     * Worker thread class for processing messages from the queue.
     */
    static class Worker extends Thread {
        public void run() {
            while (true) {
                try {
                    Message message = messageQueueTake();
                    nMessagesIn++;
                    sendToService(message);
                    nMessagesOut++;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Daemon thread class for handling message timeouts.
     */
    static class Daemon extends Thread {
        public void run() {
            while (true) {
                // Check for messages that have timed out
                for (Map.Entry<String, Message> entry : waitingForAnswer.entrySet()) {
                    Message message = entry.getValue();
                    if (message.getTimestamp().plusNanos(Timeout_ns).isBefore(LocalDateTime.now())) {
                        nTimeouts++;
                        waitingForAnswer.remove(message.getTransactionId());
                        nMessagesOut++;
                        try {
                            sendToService(message);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    // Helper methods for managing messages waiting for a response
    private static void putWaitingForAnswer(String transactionId, Message message) {
        if (transactionId == null || message == null) {
            System.err.println("Error: Transaction ID or Message is null");
            System.err.println("Sender: " + message.getSender());
            return;
        }
        lock.lock();
        try {
            waitingForAnswer.put(transactionId, message);
        } finally {
            lock.unlock();
        }
    }

    private static void removeWaitingForAnswer(String transactionId) {
        lock.lock();
        try {
            waitingForAnswer.remove(transactionId);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Distributes a message to the appropriate service based on its type.
     *
     * @param message The message to distribute.
     */
    public static void sendToService(Message message) {
        switch (message.getContent().getType()) {
            case "HotelBooking":
            case "FlightBooking":
            case "FlightCancel":
            case "HotelCancel":
                putWaitingForAnswer(message.getTransactionId(), message);
                // Logic to send message to the appropriate service
                break;
            case "Answer":
                removeWaitingForAnswer(message.getTransactionId());
                TripBroker.receiveMessage(message);
                break;
        }
    }

    /**
     * Prints statistics about message processing.
     */
    public static void printStats() {
        System.out.println("Messages in: " + nMessagesIn);
        System.out.println("Messages out: " + nMessagesOut);
        System.out.println("Timeouts: " + nTimeouts);
    }
}