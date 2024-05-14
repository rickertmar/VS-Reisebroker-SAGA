package MessageBroker;

import java.time.LocalDateTime;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.SynchronousQueue;

public class MessageBroker {
    private static SynchronousQueue<Message> messageQueue;
    //transaction id -> message needs to be concurent
    private static ConcurrentHashMap<String, Message> waitingforAnswer = new ConcurrentHashMap<String, Message>();
    private static int Timeout_ns =5000000;

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
                    sendToService(message);
                } catch (InterruptedException e) {
                    e.printStackTrace();
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
                                //remove from waiting list
                                waitingforAnswer.remove(message.getTransactionId());
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
                    //send back to original sender
                    waitingforAnswer.remove(message.getTransactionId());
                    send(message);
                    break;
            }
        }

}
}
