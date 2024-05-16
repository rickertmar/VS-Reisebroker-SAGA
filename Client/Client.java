package Client;
//random

import java.util.Random;

//tripbroker
import Tripbroker.TripBroker;

public class Client extends Thread {
    static String[] hotels= {};
    static String[] flights={};

    static int timeoutUpper = 5000;
    static int timeoutLower = 1000;

    static int maxRooms = 10;
    static int maxSeats = 10;


    public void setMaxRooms(int maxRooms) {
        this.maxRooms = maxRooms;
    }

    public void setMaxSeats(int maxSeats) {
        this.maxSeats = maxSeats;
    }

    public void setHotels(String[] hotels) {
        this.hotels = hotels;
    }

    public void setFlights(String[] flights) {
        this.flights = flights;
    }

    public void setTimeoutUpper(int timeoutUpper) {
        this.timeoutUpper = timeoutUpper;
    }

    public void setTimeoutLower(int timeoutLower) {
        this.timeoutLower = timeoutLower;
    }

    public static void addHotels(String[] hotels) {//add to original
        String[] temp = new String[Client.hotels.length + hotels.length];
        System.arraycopy(Client.hotels, 0, temp, 0, Client.hotels.length);
        System.arraycopy(hotels, 0, temp, Client.hotels.length, hotels.length);
        Client.hotels = temp;
    }

    public static void addFlights(String[] flights) {
        String[] temp = new String[Client.flights.length + flights.length];
        System.arraycopy(Client.flights, 0, temp, 0, Client.flights.length);
        System.arraycopy(flights, 0, temp, Client.flights.length, flights.length);
        Client.flights = temp;
    }

    public void run() {
        Random random = new Random();
        for (int i = 0; i < 4; i++){
            try {
                Thread.sleep(random.nextInt(timeoutUpper - timeoutLower) + timeoutLower);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            String randomHotel = hotels[random.nextInt(hotels.length)];
            String randomFlight = flights[random.nextInt(flights.length)];
            int randomNumberOfRooms = random.nextInt(maxRooms) + 1;
            int randomNumberOfSeats = random.nextInt(maxSeats) + 1;

            //send to tripbroker
            TripBroker.book(randomHotel, randomFlight, randomNumberOfRooms, randomNumberOfSeats);


        }
    }
}
