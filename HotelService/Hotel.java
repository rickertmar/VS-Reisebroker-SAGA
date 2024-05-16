package HotelService;

import java.util.Random;

public class Hotel {
    private Random random = new Random();
    public final String name;
    private int totalBeds;
    private int availableBeds;


    public Hotel(String name, int totalBeds) {
        this.name = name;
        this.totalBeds = totalBeds;
        this.availableBeds = totalBeds;
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
