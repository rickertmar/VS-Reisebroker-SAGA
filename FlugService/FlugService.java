package FlugService;

import java.util.concurrent.BlockingQueue;

public class FlugService {
    private String name;
    private int totalSeats;
    private int availableSeats;
    private BlockingQueue<test.BookingRequestFlight> bookingRequestQueue;
    private BlockingQueue<test.ConfirmationMessageFlight> confirmationMessageQueue;

    public FlugService(String name, int totalSeats, BlockingQueue<test.BookingRequestFlight> bookingRequestQueue, BlockingQueue<test.ConfirmationMessageFlight> confirmationMessageQueue) {
        this.name = name;
        this.totalSeats = totalSeats;
        this.availableSeats = totalSeats;
        this.bookingRequestQueue = bookingRequestQueue;
        this.confirmationMessageQueue = confirmationMessageQueue;
    }

    public void processRequests() {
        System.out.println("FlightService started on thread: " + Thread.currentThread().getName());
        while (true) {
            try {
                test.BookingRequestFlight request = bookingRequestQueue.take(); // Wait for a booking request
                boolean success = bookSeats(request.getNumSeats());
                // Send booking confirmation or failure message back to the message broker
                test.ConfirmationMessageFlight confirmationMessage = new test.ConfirmationMessageFlight(request.getRequestId(), success);
                confirmationMessageQueue.put(confirmationMessage);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized boolean bookSeats(int numSeats) {
        if (availableSeats >= numSeats) {
            availableSeats -= numSeats;
            System.out.println(numSeats + " seats booked successfully on flight " + name);
            System.out.println("Available seats: " + availableSeats);
            return true;
        } else {
            System.out.println("Insufficient seats available on flight " + name);
            return false;
        }
    }

    public synchronized void releaseSeats(int numSeats) {
        availableSeats += numSeats;
        System.out.println(numSeats + " seats released on flight " + name);
    }

    public synchronized int getAvailableSeats() {
        return availableSeats;
    }

    public synchronized int getTotalSeats() {
        return totalSeats;
    }
}
