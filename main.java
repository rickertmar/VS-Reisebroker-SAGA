import FlugService.FlugService;
import HotelService.HotelService;
import messagebrokersamuel.MessageBroker;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class main {
    public static void main(String[] args) {
        BlockingQueue<test.BookingRequest> bookingRequestQueue1 = new LinkedBlockingQueue<>();
        BlockingQueue<test.ConfirmationMessage> confirmationMessageQueue1 = new LinkedBlockingQueue<>();
        BlockingQueue<test.BookingRequest> bookingRequestQueue2 = new LinkedBlockingQueue<>();
        BlockingQueue<test.ConfirmationMessage> confirmationMessageQueue2 = new LinkedBlockingQueue<>();
        BlockingQueue<test.BookingRequestFlight> bookingRequestFlightQueue1 = new LinkedBlockingQueue<>();
        BlockingQueue<test.ConfirmationMessageFlight> confirmationMessageFlightQueue1 = new LinkedBlockingQueue<>();

        // Erstes Hotel erstellen
        HotelService hotel1 = new HotelService("Hotel A", 50, bookingRequestQueue1, confirmationMessageQueue1);

        // Zweites Hotel erstellen
        HotelService hotel2 = new HotelService("Hotel B", 100, bookingRequestQueue2, confirmationMessageQueue2);

        // FlugService erstellen
        FlugService flug1 = new FlugService("Flug A", 4, bookingRequestFlightQueue1, confirmationMessageFlightQueue1);

        // Message Broker erstellen und beiden Hotels zugehörige Warteschlangen übergeben
        MessageBroker messageBroker = new MessageBroker(bookingRequestQueue1, confirmationMessageQueue1, bookingRequestQueue2, confirmationMessageQueue2, bookingRequestFlightQueue1, confirmationMessageFlightQueue1);

        // Threads für die Hotels und den Message Broker starten
        ExecutorService hotelExecutor = Executors.newFixedThreadPool(2);
        hotelExecutor.execute(hotel1::processRequests);
        hotelExecutor.execute(hotel2::processRequests);

        ExecutorService flugExecutor = Executors.newFixedThreadPool(1);
        flugExecutor.execute(flug1::processRequests);

        ExecutorService brokerExecutor = Executors.newSingleThreadExecutor();
        brokerExecutor.execute(messageBroker::receiveBookingRequests);
        brokerExecutor.execute(messageBroker::sendConfirmationMessages);

        // Warte bis alle Buchungsanfragen verarbeitet wurden
        try {
            Thread.sleep(1000); // Warte eine Sekunde
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        // Shutdown der Executoren
        hotelExecutor.shutdown();
        flugExecutor.shutdown();
        brokerExecutor.shutdown();
    }
}
