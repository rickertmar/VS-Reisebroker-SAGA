package HotelService;

public class Hotel {
    public final String name;
    private int totalBeds;
    private int availableBeds;


    public Hotel(String name, int totalBeds) {
        this.name = name;
        this.totalBeds = totalBeds;
        this.availableBeds = totalBeds;
    }

    /**
     * Attempts to book a specified number of rooms (beds) in the hotel.
     * If sufficient beds are available, books the beds and returns true.
     * Otherwise, booking fails and returns false.
     *
     * @param numRooms The number of rooms (beds) to book.
     * @return true if the booking is successful, false otherwise.
     */
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

    /**
     * Releases a specified number of rooms (beds), making them available for booking again.
     * This method is used to cancel bookings and increase the number of available beds.
     *
     * @param numRooms The number of rooms (beds) to release.
     */
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
