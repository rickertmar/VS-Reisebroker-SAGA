package DataFiller;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import FlightService.Flight;
import HotelService.Hotel;

/**
 * The Generator class is responsible for populating lists of Hotel and Flight objects
 * with data loaded from a properties file. This class demonstrates how to read from a
 * properties file and create corresponding Java objects based on the data.
 */
public class Generator {
    /**
     * The main method of the Generator class. It triggers the filling of hotels and flights
     * with data from the properties file.
     * @param args Command line arguments (not used).
     */
    public static void main(String[] args) {
        fillHotels();
        fillFlights();
    }

    /**
     * Reads hotel data from the properties file and creates a list of Hotel objects.
     * Each property with a key starting with "Hotel" is considered a hotel entry.
     * @return A list of Hotel objects populated with data from the properties file.
     */
    public static List<Hotel> fillHotels() {
        List<Hotel> hotels = new ArrayList<>();
        try (InputStream inputStream = Generator.class.getResourceAsStream("/data.properties")) {
            Properties properties = new Properties();
            properties.load(inputStream);

            properties.forEach((key, value) -> {
                if (key.toString().startsWith("Hotel")) {
                    String hotelName = key.toString();
                    String bedsString = value.toString().split(":")[1].trim(); // Extracts the number of beds
                    int numberOfBeds = Integer.parseInt(bedsString);
                    hotels.add(new Hotel(hotelName, numberOfBeds));
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return hotels;
    }

    /**
     * Reads flight data from the properties file and creates a list of Flight objects.
     * Each property with a key starting with "Flight" is considered a flight entry.
     * @return A list of Flight objects populated with data from the properties file.
     */
    public static List<Flight> fillFlights() {
        List<Flight> flights = new ArrayList<>();
        try (InputStream inputStream = Generator.class.getResourceAsStream("/data.properties")) {
            Properties properties = new Properties();
            properties.load(inputStream);

            properties.forEach((key, value) -> {
                if (key.toString().startsWith("Flight")) {
                    String flightName = key.toString();
                    String seatsString = value.toString().split(":")[1].trim(); // Extracts the number of seats
                    int numberOfSeats = Integer.parseInt(seatsString);
                    flights.add(new Flight(flightName, numberOfSeats));
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return flights;
    }
}