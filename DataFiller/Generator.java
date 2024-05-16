package DataFiller;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;


import FlightService.Flight;
import HotelService.Hotel;

//
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
                String hotelName = key.toString();
                if (hotelName.startsWith("Hotel")) {
                    int capacity = Integer.parseInt(value.toString().split(":")[1].trim());
                    hotels.add(new Hotel(hotelName, capacity));
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
                String flightName = key.toString();
                if (flightName.startsWith("Flight")) {
                    int capacity = Integer.parseInt(value.toString().split(":")[1].trim());
                    flights.add(new Flight(flightName, capacity));
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return flights;
    }
}
