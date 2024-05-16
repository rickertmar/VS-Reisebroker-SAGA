package DataFiller;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import FlightService.Flight;
import HotelService.Hotel;

public class Generator {
    public static void main(String[] args) {
        fillHotels();
        fillFlights();
    }

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

    public static List<Flight> fillFlights() {
        List<Flight> flights = new ArrayList<>();
        try (InputStream inputStream = Generator.class.getResourceAsStream("/data.properties")) {
            Properties properties = new Properties();
            properties.load(inputStream);

            properties.forEach((key, value) -> {
                if (key.toString().startsWith("Flight")) {
                    String flightName = key.toString();
                    String seatsString = value.toString().split(":")[1].trim();
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
