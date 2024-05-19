package Client;

import java.util.Random;

import Tripbroker.TripBroker;

/**
 * The Client class simulates a client making booking requests.
 * It extends the Thread class, allowing each client to operate in its own thread.
 */
public class Client extends Thread {
    // Arrays to store available hotels and flights
    static String[] hotels = {};
    static String[] flights = {};

    // Timeout bounds for simulating real-world request delays
    static int timeoutUpper = 50;
    static int timeoutLower = 10;

    // Maximum number of rooms and seats that can be booked
    static int maxRooms = 10;
    static int maxSeats = 10;

    /**
     * Sets the maximum number of rooms that can be booked.
     * @param maxRooms Maximum number of rooms.
     */
    public void setMaxRooms(int maxRooms) {
        this.maxRooms = maxRooms;
    }

    /**
     * Sets the maximum number of seats that can be booked.
     * @param maxSeats Maximum number of seats.
     */
    public void setMaxSeats(int maxSeats) {
        this.maxSeats = maxSeats;
    }

    /**
     * Sets the available hotels.
     * @param hotels Array of hotel identifiers.
     */
    public void setHotels(String[] hotels) {
        this.hotels = hotels;
    }

    /**
     * Sets the available flights.
     * @param flights Array of flight identifiers.
     */
    public void setFlights(String[] flights) {
        this.flights = flights;
    }

    /**
     * Sets the upper bound for request timeout.
     * @param timeoutUpper Upper bound in milliseconds.
     */
    public void setTimeoutUpper(int timeoutUpper) {
        this.timeoutUpper = timeoutUpper;
    }

    /**
     * Sets the lower bound for request timeout.
     * @param timeoutLower Lower bound in milliseconds.
     */
    public void setTimeoutLower(int timeoutLower) {
        this.timeoutLower = timeoutLower;
    }

    /**
     * Adds additional hotels to the list of available hotels.
     * @param hotels Array of new hotel identifiers to add.
     */
    public static void addHotels(String[] hotels) {
        String[] temp = new String[Client.hotels.length + hotels.length];
        System.arraycopy(Client.hotels, 0, temp, 0, Client.hotels.length);
        System.arraycopy(hotels, 0, temp, Client.hotels.length, hotels.length);
        Client.hotels = temp;
    }

    /**
     * Adds additional flights to the list of available flights.
     * @param flights Array of new flight identifiers to add.
     */
    public static void addFlights(String[] flights) {
        String[] temp = new String[Client.flights.length + flights.length];
        System.arraycopy(Client.flights, 0, temp, 0, Client.flights.length);
        System.arraycopy(flights, 0, temp, Client.flights.length, flights.length);
        Client.flights = temp;
    }

    /**
     * The main execution method for the client thread.
     * Simulates booking requests by randomly selecting hotels and flights and making booking requests.
     */
    public void run() {
        Random random = new Random();
        for (int i = 0; i < 20; i++) {
            try {
                // Simulate a delay between requests
                Thread.sleep(random.nextInt(timeoutUpper - timeoutLower) + timeoutLower);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // Randomly select a hotel and a flight
            String randomHotel = hotels[random.nextInt(hotels.length)];
            String randomFlight = flights[random.nextInt(flights.length)];
            // Randomly determine the number of rooms and seats to book
            int randomNumberOfRooms = random.nextInt(maxRooms) + 1;
            int randomNumberOfSeats = random.nextInt(maxSeats) + 1;

            // Make the booking request
            TripBroker.book(randomHotel, randomFlight, randomNumberOfRooms, randomNumberOfSeats);
        }
    }
}