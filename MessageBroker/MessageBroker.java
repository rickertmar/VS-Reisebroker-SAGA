package MessageBroker;

import HotelService.HotelService;
import FlightService.FlightService;
import Tripbroker.TripBroker;
import MessageBroker.MessageQueue;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class MessageBroker {
    static ReentrantLock lock = new ReentrantLock();
    static int nMessagesIn = 0;
    static int nTimeouts = 0;
    static int nMessagesOut = 0;
    // Ring buffer for messages
    private static MessageQueue messageQueue = new MessageQueue(100);

    // Transaction id -> message needs to be concurrent
    private static ConcurrentHashMap<String, Message> waitingForAnswer = new ConcurrentHashMap<>();
    private static int Timeout_ns = 0;
    private static ConcurrentHashMap<String, HotelService> HotelAddresses = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<String, FlightService> FlightAddresses = new ConcurrentHashMap<>();

    public static void init() {
        Worker worker = new Worker();
        worker.start();
        Daemon daemon = new Daemon();
        daemon.start();
    }

    public static void registerHotelService(String name, HotelService hotelService) {
        HotelAddresses.put(name, hotelService);
    }

    public static void registerFlightService(String name, FlightService flightService) {
        FlightAddresses.put(name, flightService);
    }

    public static void send(Message message) {
        try {
            messageQueuePut(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void messageQueuePut(Message message) throws InterruptedException {
        messageQueue.messageQueuePut(message);
    }

    private static Message messageQueueTake() throws InterruptedException {
        return messageQueue.messageQueueTake();
    }

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

    static class Daemon extends Thread {
        public void run() {
            while (true) {
                // Get all messages waiting for answer
                for (Map.Entry<String, Message> entry : waitingForAnswer.entrySet()) {
                    Message message = entry.getValue();
                    if (message.getTimestamp().plusNanos(Timeout_ns).isBefore(LocalDateTime.now())) {
                        nTimeouts++;
                        // Remove from waiting list
                        waitingForAnswer.remove(message.getTransactionId());
                        nMessagesOut++;
                        // Send back to original sender
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

    private static void putWaitingForAnswer(String transactionId, Message message) {
        if (transactionId == null || message == null) {
            System.err.println("Error: Transaction ID or Message is null");
            System.err.println("Sender: " + message.getSender());
            return;
        }
        lock.lock();
        try {
            System.out.println("Putting message in waiting list");
            System.out.println("Transaction id: " + transactionId);
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

    public static void sendToService(Message message) {
        switch (message.getContent().getType()) {
            case "HotelBooking":
                putWaitingForAnswer(message.getTransactionId(), message);
                HotelService hotelService = HotelAddresses.get(message.getRecipient());
                hotelService.receiveMessage(message);
                break;
            case "FlightBooking":
                putWaitingForAnswer(message.getTransactionId(), message);
                FlightService flightService = FlightAddresses.get(message.getRecipient());
                flightService.receiveMessage(message);
                break;
            case "FlightCancel":
                putWaitingForAnswer(message.getTransactionId(), message);
                FlightService flightService1 = FlightAddresses.get(message.getRecipient());
                flightService1.receiveMessage(message);
                break;
            case "HotelCancel":
                putWaitingForAnswer(message.getTransactionId(), message);
                HotelService hotelService1 = HotelAddresses.get(message.getRecipient());
                hotelService1.receiveMessage(message);
                break;
            case "Answer":
                System.out.println("Sending answer back to Trip Broker");
                removeWaitingForAnswer(message.getTransactionId());
                TripBroker.receiveMessage(message);
                break;
        }
    }

    public static void printStats() {
        System.out.println("Messages in: " + nMessagesIn);
        System.out.println("Messages out: " + nMessagesOut);
        System.out.println("Timeouts: " + nTimeouts);
    }
}
