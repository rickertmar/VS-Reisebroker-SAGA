package FlightService;

import java.util.Random;

public class Flight {
    public final String name;
    private final int totalSeats;
    private int availableSeats;


    public Flight(String name, int totalSeats) {
        this.name = name;
        this.totalSeats = totalSeats;
        this.availableSeats = totalSeats;
    }

    public synchronized boolean bookSeats(int numSeats) {
        if (numSeats <= availableSeats) {
            availableSeats -= numSeats;
            System.out.println(numSeats + " seats booked at " + name);
            return true;
        }
        System.out.println("Failed to book " + numSeats + " seats at " + name);
        return false;
    }

    public synchronized void releaseSeats(int numRooms) {
        availableSeats += numRooms;
        System.out.println(numRooms + " rooms released at " + name);
    }

    public synchronized int getAvailableSeats() {
        return availableSeats;
    }

    public synchronized int getTotalSeats() {
        return totalSeats;
    }
}
