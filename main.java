import FlightService.FlightService;
import HotelService.HotelService;
import MessageBroker.MessageBroker;

import Tripbroker.TripBroker;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import DataFiller.Generator;
import FlightService.Flight;
import HotelService.Hotel;

public class main {
    public static void main(String[] args) {
        // Create a trip broker
        TripBroker tripBroker = new TripBroker();
        // Create a message broker
        MessageBroker messageBroker = new MessageBroker();
        // Initialize the message broker
        messageBroker.init(tripBroker);



        //create 2 flight services
        Flight[] flights1 = Generator.fillFlights();//sm like that
        FlightService flightService1 = new FlightService("FlightService1", flights1);
        Flight[] flights2 = Generator.fillFlights();//sm like that
        FlightService flightService2 = new FlightService("FlightService2", flights2);

        // Register the flight services with the message broker
        messageBroker.registerFlightService("FlightService1", flightService1);
        messageBroker.registerFlightService("FlightService2", flightService2);


        //create 2 hotel services
        Hotel[] hotels1 = Generator.fillHotels();//sm like that
        HotelService hotelService1 = new HotelService("HotelService1", hotels1);
        Hotel[] hotels2 = Generator.fillHotels();//sm like that
        HotelService hotelService2 = new HotelService("HotelService2", hotels2);

        // Register the hotel services with the message broker
        messageBroker.registerHotelService("HotelService1", hotelService1);
        messageBroker.registerHotelService("HotelService2", hotelService2);







        ///tsetclient macht anfrage indm er







    }
}
