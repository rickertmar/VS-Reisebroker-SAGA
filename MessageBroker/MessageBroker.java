package MessageBroker;

import HotelService.HotelService;
import FlightService.FlightService;

import Tripbroker.TripBroker;

import java.time.LocalDateTime;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.SynchronousQueue;

public class MessageBroker {

    static int nMessagesIn = 0;
    static int nTimeouts = 0;
    static int nMessagesOut = 0;
    private static SynchronousQueue<Message> messageQueue;
    //transaction id -> message needs to be concurent
    private static ConcurrentHashMap<String, Message> waitingforAnswer = new ConcurrentHashMap<String, Message>();
    private static int Timeout_ns =5000000;
    private static ConcurrentHashMap<String, HotelService> HotelAdresses = new ConcurrentHashMap<String, HotelService>();
    private static ConcurrentHashMap<String, FlightService> FlightAdresses = new ConcurrentHashMap<String, FlightService>();


    public static void init() {
        messageQueue = new SynchronousQueue<Message>();
        Worker worker = new Worker();
        worker.start();
        Deamon deamon = new Deamon();
        deamon.start();
    }
    public static void registerHotelService(String name, HotelService hotelService) {
        HotelAdresses.put(name, hotelService);
    }

    public static void registerFlightService(String name, FlightService flightService) {
        FlightAdresses.put(name, flightService);
    }

    public static void send(Message message) {
        try {
            messageQueue.put(message);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    static class Worker extends Thread {
        public void run() {
            while (true) {

                try {
                    Message message = messageQueue.take();
                    nMessagesIn++;
                    sendToService(message);
                    nMessagesOut++;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
        static class Deamon extends Thread {
            public void run() {
                while (true) {
                    try {
                        // get all messages waiting for answer
                        for (Map.Entry<String, Message> entry : waitingforAnswer.entrySet()) {
                            Message message = entry.getValue();
                            if (message.getTimestamp().plusNanos(Timeout_ns).isBefore(LocalDateTime.now())) {
                                nTimeouts++;
                                //remove from waiting list
                                waitingforAnswer.remove(message.getTransactionId());
                                nMessagesOut++;
                                //send back to original sender
                                sendToService(message);
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        static void sendToService(Message message) {
            //todo send to service
            switch (message.getContent().getType()) {
                case "HotelBooking":
                    //line to send to hotel service
                    waitingforAnswer.put(message.getTransactionId(), message);
                    HotelService hotelService = HotelAdresses.get(message.getRecipient());
                    hotelService.receiveMessage(message);
                    break;
                case "FlightBooking":
                    //line to send to flight service
                    waitingforAnswer.put(message.getTransactionId(), message);
                    FlightService flightService = FlightAdresses.get(message.getRecipient());
                    flightService.receiveMessage(message);
                    break;
                case "FlightCancel":
                    //line to send to flight service
                    waitingforAnswer.put(message.getTransactionId(), message);
                    FlightService flightService1 = FlightAdresses.get(message.getRecipient());
                    flightService1.receiveMessage(message);
                    break;

                case "HotelCancel":
                    //line to send to hotel service
                    waitingforAnswer.put(message.getTransactionId(), message);
                    HotelService hotelService1 = HotelAdresses.get(message.getRecipient());
                    hotelService1.receiveMessage(message);
                case "Answer":
                    //send back to original sender ( allways trip broker)
                    waitingforAnswer.remove(message.getTransactionId());
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

