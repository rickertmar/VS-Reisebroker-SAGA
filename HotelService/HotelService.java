package HotelService;

import java.util.concurrent.BlockingQueue;

public class HotelService {

    private String name;
    private int totalBeds;
    private int availableBeds;
    private BlockingQueue<test.BookingRequest> bookingRequestQueue;
    private BlockingQueue<test.ConfirmationMessage> confirmationMessageQueue;

    public HotelService(String name, int totalBeds, BlockingQueue<test.BookingRequest> bookingRequestQueue, BlockingQueue<test.ConfirmationMessage> confirmationMessageQueue) {
        this.name = name;
        this.totalBeds = totalBeds;
        this.availableBeds = totalBeds;
        this.bookingRequestQueue = bookingRequestQueue;
        this.confirmationMessageQueue = confirmationMessageQueue;
    }

    public void processRequests() {
        System.out.println("HotelService started on thread: " + Thread.currentThread().getName());
        while (true) {
            try {
                test.BookingRequest request = bookingRequestQueue.take(); // Wait for a booking request
                boolean success = bookRooms(request.getNumRooms());
                // Send booking confirmation or failure message back to the message broker
                test.ConfirmationMessage confirmationMessage = new test.ConfirmationMessage(request.getRequestId(), success);
                confirmationMessageQueue.put(confirmationMessage);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized boolean bookRooms(int numRooms) {
        if (availableBeds >= numRooms) {
            availableBeds -= numRooms;
            System.out.println(numRooms + " rooms booked successfully at " + name);
            System.out.println("Available beds: " + availableBeds);
            return true;
        } else {
            System.out.println("Insufficient rooms available at " + name);
            return false;
        }
    }

    public synchronized void releaseRooms(int numRooms) {
        availableBeds += numRooms;
        System.out.println(numRooms + " rooms released at " + name);
    }

    public synchronized int getAvailableBeds() {
        return availableBeds;
    }

    public synchronized int getTotalBeds() {
        return totalBeds;
    }
}
