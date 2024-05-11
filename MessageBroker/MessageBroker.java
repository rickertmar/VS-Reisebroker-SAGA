package MessageBroker;

import java.util.HashMap;
import java.util.concurrent.BlockingQueue;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.SynchronousQueue;

public class MessageBroker {
    private static SynchronousQueue<Message> messageQueue;

    //transaction id -> message needs to be concurent
    private static ConcurrentHashMap<String, Message> waitingforAnswer = new ConcurrentHashMap<String, Message>();


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


                    switch (message.getContent().getClass().getName()) {
                        case "ComboBooking":
                            FlightBooking flightBooking = ((ComboBooking) message.getContent()).getFlightBooking();
                            //line to send to flight service
                            waitingforAnswer.put(message.getTransactionId(), message);

                            HotelBooking hotelBooking = ((ComboBooking) message.getContent()).getHotelBooking();
                            //line to send to hotel service
                            waitingforAnswer.put(message.getTransactionId(), message);
                            break;
                        case "HotelBooking":
                            //line to send to hotel service
                            waitingforAnswer.put(message.getTransactionId(), message);
                            break;
                        case "FlightBooking":
                            //line to send to flight service
                            waitingforAnswer.put(message.getTransactionId(), message);
                            break;
                        case "Answer":
                            Message originalMessage = waitingforAnswer.get(message.getTransactionId());
                            //remove from waiting list
                            waitingforAnswer.remove(message.getTransactionId());
                            //send back to original sender
                            break;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
       static class Deamon extends Thread {
            public void run() {
                while (true) {
                    try {
                        //take RANDOM message from waitingforAnswer
                        //if it is older than 5 seconds
                        //send back to original sender
                        //after sending set timestamp to now


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
}











}
