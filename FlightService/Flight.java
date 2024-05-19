package FlightService;

public class Flight {
    public final String name;
    private final int totalSeats;
    private int availableSeats;

    /**
     * Constructs a Flight instance with a specified name and total number of seats.
     * Initially, all seats are available for booking.
     *
     * @param name The name of the flight.
     * @param totalSeats The total number of seats available on the flight.
     */
    public Flight(String name, int totalSeats) {
        this.name = name;
        this.totalSeats = totalSeats;
        this.availableSeats = totalSeats;
    }

    /**
     * Attempts to book a specified number of seats on the flight.
     * If sufficient seats are available, books the seats and returns true.
     * Otherwise, booking fails and returns false.
     *
     * @param numSeats The number of seats to book.
     * @return true if the booking is successful, false otherwise.
     */
    public synchronized boolean bookSeats(int numSeats) {
        if (numSeats <= availableSeats) {
            availableSeats -= numSeats;
            System.out.println(numSeats + " seats booked at " + name);
            return true;
        }
        System.out.println("Failed to book " + numSeats + " seats at " + name);
        return false;
    }

    /**
     * Releases a specified number of seats, making them available for booking again.
     * This method is used to cancel bookings and increase the number of available seats.
     *
     * @param numRooms The number of seats to release.
     */
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