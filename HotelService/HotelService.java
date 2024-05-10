package HotelService;

import java.util.concurrent.BlockingQueue;
import java.util.Random;
import java.util.HashMap;
import java.util.Map;


public class HotelService {
    public final String name;
    private Hotel[] hotels;
    //map transaction id to answer message
    private Map<String, test.BookingRequest> waitingForAnswer = new HashMap<String, test.BookingRequest>();

    public HotelService(String name,int numHotels) {
        this.name = name;
        hotels = new Hotel[numHotels];
        for (int i = 0; i < hotels.length; i++) {
            hotels[i] = new Hotel();
        }
    }




    public void recieveMessage(test.BookingRequest bookingRequest) {
        //random chance to fail -> return

        //check map if transaction id is already there
        //if not add to map

        //execute request
        //map awaits answer
                //if type is booking request
                //if type is cancel request


        //send message to message broker
    }

    public void sendMessage(Boolean success, String transactionId) {
        //random chance to fail -> return

        //send ANSWER message to message broker
    }




}

private class Hotel {
    private  Random random = new Random();
    private String name;
    private int totalBeds;
    private int availableBeds;
    private BlockingQueue<test.BookingRequest> bookingRequestQueue;
    private BlockingQueue<test.ConfirmationMessage> confirmationMessageQueue;

    public Hotel() {
        // randomized initial available beds
        this.name = "Hotel " + random.nextInt(1000);
        this.totalBeds = random.nextInt(100,200);
        this.availableBeds = random.nextInt(50,100);
        this.bookingRequestQueue = new blockingQueue<test.BookingRequest>();
        this.confirmationMessageQueue = new blockingQueue<test.ConfirmationMessage>();
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
