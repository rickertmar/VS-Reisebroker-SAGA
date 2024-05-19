import DataFiller.FillProperties;
import FlightService.FlightService;
import HotelService.HotelService;
import MessageBroker.MessageBroker;

import Tripbroker.TripBroker;

import Client.Client;

import DataFiller.Generator;
import FlightService.Flight;
import HotelService.Hotel;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        // init message broker
        MessageBroker.init();


        // create 2 flight services
        Flight[] flights = Generator.fillFlights().toArray(new Flight[0]);
        Flight[] flights1 = new Flight[flights.length / 2];
        Flight[] flights2 = new Flight[flights.length - flights1.length];
        System.arraycopy(flights, 0, flights1, 0, flights1.length);
        System.arraycopy(flights, flights1.length, flights2, 0, flights2.length);

        FlightService flightService1 = new FlightService("FlightService1", flights1);
        FlightService flightService2 = new FlightService("FlightService2", flights2);

        // create 2 hotel services
        Hotel[] hotels = Generator.fillHotels().toArray(new Hotel[0]);
        Hotel[] hotels1 = new Hotel[hotels.length / 2];
        Hotel[] hotels2 = new Hotel[hotels.length - hotels1.length];
        System.arraycopy(hotels, 0, hotels1, 0, hotels1.length);
        System.arraycopy(hotels, hotels1.length, hotels2, 0, hotels2.length);

        HotelService hotelService1 = new HotelService("HotelService1", hotels1);
        HotelService hotelService2 = new HotelService("HotelService2", hotels2);

        // register services
        MessageBroker.registerFlightService(flightService1.getName(), flightService1);
        MessageBroker.registerFlightService(flightService2.getName(), flightService2);

        MessageBroker.registerHotelService(hotelService1.getName(), hotelService1);
        MessageBroker.registerHotelService(hotelService2.getName(), hotelService2);

        // register hotels and flights in tripbroker
        TripBroker.addFlights(flightService1.getFlights(), flightService1.getName());
        TripBroker.addFlights(flightService2.getFlights(), flightService2.getName());

        TripBroker.addHotels(hotelService1.getHotels(), hotelService1.getName());
        TripBroker.addHotels(hotelService2.getHotels(), hotelService2.getName());

        // add flights to Clients
        Client.addFlights(flightService1.getFlights());
        Client.addFlights(flightService2.getFlights());
        // add hotels to Clients
        Client.addHotels(hotelService1.getHotels());
        Client.addHotels(hotelService2.getHotels());

        Client[] clients = new Client[FillProperties.getNoClients()];
        // instance  clients
        for (int i = 0; i < FillProperties.getNoClients(); i++) {
            clients[i] = new Client();
            clients[i].start();
        }


        // wait for clients to finish
        for (int i = 0; i < FillProperties.getNoClients(); i++) {
            try {
                clients[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // print stats
        System.out.println("TripBroker stats:");
        TripBroker.printStats();
        System.out.println("Message broker stats:");
        MessageBroker.printStats();


    }
}
