package MessageBroker;

import java.util.Map;

public class MessageBroker {
    Synchronized blockingQueue messageQueue = new blockingQueue<Message>();
    //transaction id -> message
    Synchronized Map<> waitingforAnswer = new HashMap<String, Message>();

    //todo methods for synchronizing

    class Worker extends Thread {
        public void run() {
            while (true) {
                try {
                    Message message = messageQueue.take();


                    switch (message.getContent().getClass().getName()) {
                        case "ComboBooking":
                            FlightBooking flightBooking = ((ComboBooking) message.getContent()).flightBooking;
                            //line to send to flight service
                            waitingforAnswer.put(message.getTransactionId(), message);

                            HotelBooking hotelBooking = ((ComboBooking) message.getContent()).hotelBooking;
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
        class Deamon extends Thread {
            public void run() {
                while (true) {
                    try {
                        //take RANDOM message from waitingforAnswer
                        //if it is older than 5 seconds
                        //send back to original sender
                        //after sending set timestamp to now


                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
}











}
