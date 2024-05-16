import DataFiller.FillProperties;
import FlightService.FlightService;
import HotelService.HotelService;
import MessageBroker.MessageBroker;

import Tripbroker.TripBroker;

import Client.Client;

import DataFiller.Generator;
import FlightService.Flight;
import HotelService.Hotel;

public class Main {
    public static void main(String[] args) {
        //init message broker
        MessageBroker.init();


        //create 2 flight services
        Flight[] flights1 = Generator.fillFlights();//sm like that
        FlightService flightService1 = new FlightService("FlightService1", flights1);
        Flight[] flights2 = Generator.fillFlights();//sm like that
        FlightService flightService2 = new FlightService("FlightService2", flights2);

        //create 2 hotel services
        Hotel[] hotels1 = Generator.fillHotels();//sm like that
        HotelService hotelService1 = new HotelService("HotelService1", hotels1);
        Hotel[] hotels2 = Generator.fillHotels();//sm like that
        HotelService hotelService2 = new HotelService("HotelService2", hotels2);

        //register services
        MessageBroker.registerFlightService(flightService1.getName(), flightService1);
        MessageBroker.registerFlightService(flightService2.getName(), flightService2);

        MessageBroker.registerHotelService(hotelService1.getName(), hotelService1);
        MessageBroker.registerHotelService(hotelService2.getName(), hotelService2);

        //register hotels and flights in tripbroker
        TripBroker.addFlights(flightService1.getFlights(), flightService1.getName());
        TripBroker.addFlights(flightService2.getFlights(), flightService2.getName());

        TripBroker.addHotels(hotelService1.getHotels(), hotelService1.getName());
        TripBroker.addHotels(hotelService2.getHotels(), hotelService2.getName());

        Client[] clients = new Client[FillProperties.getNoClients()];
        //instance  clients
        for (int i = 0; i < FillProperties.getNoClients(); i++) {
            clients[i] = new Client();
            clients[i].start();
        }


        //wait for clients to finish
        for (int i = 0; i < FillProperties.getNoClients(); i++) {
            try {
                clients[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        //print stats
        TripBroker.printStats();


    }
}
