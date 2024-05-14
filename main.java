import FlightService.FlightService;
import HotelService.HotelService;
import MessageBroker.MessageBroker;

import Tripbroker.TripBroker;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class main {
    public static void main(String[] args) {
        // Create a trip broker
        TripBroker tripBroker = new TripBroker();
        // Create a message broker
        MessageBroker messageBroker = new MessageBroker();
        // Initialize the message broker
        messageBroker.init(tripBroker);

        //create 2 flight services
        FlightService flightService1 = new FlightService();


    }
}
