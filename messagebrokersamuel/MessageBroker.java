package messagebrokersamuel;

import java.util.concurrent.BlockingQueue;

public class MessageBroker {


    private BlockingQueue<test.BookingRequest> bookingRequestQueue1;
    private BlockingQueue<test.ConfirmationMessage> confirmationMessageQueue1;
    private BlockingQueue<test.BookingRequest> bookingRequestQueue2;
    private BlockingQueue<test.ConfirmationMessage> confirmationMessageQueue2;
    private BlockingQueue<test.BookingRequestFlight> bookingRequestFlightQueue1;
    private BlockingQueue<test.ConfirmationMessageFlight> confirmationMessageFlightQueue1;

    public MessageBroker(BlockingQueue<test.BookingRequest> bookingRequestQueue1, BlockingQueue<test.ConfirmationMessage> confirmationMessageQueue1,
                         BlockingQueue<test.BookingRequest> bookingRequestQueue2, BlockingQueue<test.ConfirmationMessage> confirmationMessageQueue2,
                         BlockingQueue<test.BookingRequestFlight> bookingRequestFlightQueue1, BlockingQueue<test.ConfirmationMessageFlight> confirmationMessageFlightQueue1) {
        this.bookingRequestQueue1 = bookingRequestQueue1;
        this.confirmationMessageQueue1 = confirmationMessageQueue1;
        this.bookingRequestQueue2 = bookingRequestQueue2;
        this.confirmationMessageQueue2 = confirmationMessageQueue2;
        this.bookingRequestFlightQueue1 = bookingRequestFlightQueue1;
        this.confirmationMessageFlightQueue1 = confirmationMessageFlightQueue1;
    }

    public void receiveBookingRequests() {
        System.out.println("Message Broker started on thread: " + Thread.currentThread().getName());
        try {
            // Continuously send booking requests
            int requestId = 0;
            for (int i = 0; i < 4; i++) {
                // Simulate a booking request with 2 rooms for each hotel
                test.BookingRequest request1 = new test.BookingRequest(requestId, 2);
                test.BookingRequest request2 = new test.BookingRequest(requestId, 2);
                test.BookingRequestFlight requestFlight1 = new test.BookingRequestFlight(requestId, 2);
                bookingRequestQueue1.put(request1);
                bookingRequestQueue2.put(request2);
                bookingRequestFlightQueue1.put(requestFlight1);
                requestId++;
                Thread.sleep(1000); // wait for 1 second before sending the next request
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void sendConfirmationMessages() {
        try {
            for(int i = 0; i < 4; i++) {
                test.BookingRequest request1 = bookingRequestQueue1.take(); // Wait for a booking request for hotel 1
                test.BookingRequest request2 = bookingRequestQueue2.take();// Wait for a booking request for hotel 2
                test.BookingRequestFlight requestFlight1 = bookingRequestFlightQueue1.take();// Wait for a booking request for flight 1
                // Simulate booking success for both hotels
                boolean success1 = true;
                boolean success2 = true;
                boolean successFlight1 = true;
                test.ConfirmationMessage confirmationMessage1 = new test.ConfirmationMessage(request1.getRequestId(), success1);
                test.ConfirmationMessage confirmationMessage2 = new test.ConfirmationMessage(request2.getRequestId(), success2);
                test.ConfirmationMessageFlight confirmationMessageFlight1 = new test.ConfirmationMessageFlight(requestFlight1.getRequestId(), successFlight1);
                confirmationMessageQueue1.put(confirmationMessage1);
                confirmationMessageQueue2.put(confirmationMessage2);
                confirmationMessageFlightQueue1.put(confirmationMessageFlight1);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}
