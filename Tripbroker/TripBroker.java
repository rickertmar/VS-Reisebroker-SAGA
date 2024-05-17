package Tripbroker;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import MessageBroker.Message;
import MessageBroker.HotelBooking;
import MessageBroker.FlightBooking;
import MessageBroker.Answer;
import MessageBroker.FlightCancel;
import MessageBroker.HotelCancel;
import MessageBroker.MessageBroker;

/**
 * Status enum to represent the various states of a booking.
 */
enum Status {
    HotelPending,
    HotelConfirmed,
    HotelCanceled,
    FlightPending,
    FlightConfirmed,
    FlightCanceled,
}

/**
 * The TripBroker class manages bookings, including adding flights and hotels, receiving messages,
 * and handling the booking process.
 */
public class TripBroker {

    public static final String name = "TripBroker";

    private static int nTransactions = 0;
    private static int nTransactionsCompleted = 0;
    private static int nTransactionsFailed = 0;
    private static int nFlightscanceled = 0;  // Corrected variable name
    private static int nHotelscanceled = 0;

    private static Map<String, ComboBooking> UUIDtoBookingMap = new ConcurrentHashMap<>();
    private static Map<String, String> HotelToServiceMap = new ConcurrentHashMap<>();
    private static Map<String, String> FlightToServiceMap = new ConcurrentHashMap<>();

    /**
     * Adds flights to the service map.
     *
     * @param flights Array of flight identifiers.
     * @param service The service provider for the flights.
     */
    public static void addFlights(String[] flights, String service) {
        for (String flight : flights) {
            FlightToServiceMap.put(flight, service);
        }
    }

    /**
     * Adds hotels to the service map.
     *
     * @param hotels  Array of hotel identifiers.
     * @param service The service provider for the hotels.
     */
    public static void addHotels(String[] hotels, String service) {
        for (String hotel : hotels) {
            HotelToServiceMap.put(hotel, service);
        }
    }

    /**
     * Retrieves an array of all hotels.
     *
     * @return Array of hotel identifiers.
     */
    public static String[] getHotels() {
        return HotelToServiceMap.keySet().toArray(new String[0]);
    }

    /**
     * Processes received messages and updates booking status accordingly.
     *
     * @param message The message received from the message broker.
     */
    public static void receiveMessage(Message message) {
        String transactionId = message.getTransactionId();
        ComboBooking comboBooking = UUIDtoBookingMap.get(transactionId);
        if (comboBooking == null) {
            System.out.println("Transaction not found");
            return;
        }
        if (message.getContent().getType().equals("Answer")) {
            Answer answer = (Answer) message.getContent();
            Status status = comboBooking.status;
            switch (status) {
                case HotelPending:
                    handleHotelPending(comboBooking, answer);
                    break;

                case HotelCanceled:
                    break;

                case HotelConfirmed:
                    break;
                case FlightPending:
                    handleFlightPending(comboBooking, answer);
                    break;
                case FlightCanceled:
                    break;
                case FlightConfirmed:
                    break;
                default:
                    System.out.println("Invalid Status for Answer");
            }
        }
    }

    /**
     * Handles the hotel pending status by either confirming the booking or canceling it based on the answer.
     *
     * @param comboBooking The current combo booking.
     * @param answer       The answer received regarding the hotel booking.
     */
    private static void handleHotelPending(ComboBooking comboBooking, Answer answer) {
        if (answer.isSuccess()) {
            comboBooking.HotelAnswer(true);
            Message flightMessage = new Message(comboBooking.TransactionID, name, FlightToServiceMap.get(comboBooking.flightBooking.getFlightNumber()), comboBooking.flightBooking);
            sendToMessageBroker(flightMessage);
            comboBooking.status = Status.FlightPending;
        } else {
            comboBooking.HotelAnswer(false);
            nTransactionsFailed++;
            nHotelscanceled++;
        }
    }

    /**
     * Handles the flight pending status by either confirming the booking or canceling it based on the answer.
     *
     * @param comboBooking The current combo booking.
     * @param answer       The answer received regarding the flight booking.
     */
    private static void handleFlightPending(ComboBooking comboBooking, Answer answer) {
        if (answer.isSuccess()) {
            comboBooking.FlightAnswer(true);
            nTransactionsCompleted++;
        } else {
            comboBooking.FlightAnswer(false);
            nTransactionsFailed++;
            nFlightscanceled++;  // Correctly increment flight cancel count
            // Cancel the hotel booking
            Message cancelMessage = new Message(comboBooking.TransactionID, name, HotelToServiceMap.get(comboBooking.hotelBooking.getHotelName()), new HotelCancel(comboBooking.hotelBooking.getHotelName(), comboBooking.hotelBooking.getNumberOfRooms()));
            sendToMessageBroker(cancelMessage);
        }
    }

    /**
     * Prints statistics about the transactions processed.
     */
    public static void printStats() {
        System.out.println("Number of Transactions: " + nTransactions);
        System.out.println("Number of Transactions Completed: " + nTransactionsCompleted);
        System.out.println("Number of Transactions Failed: " + nTransactionsFailed);
        System.out.println("Number of Flights Canceled: " + nFlightscanceled);
        System.out.println("Number of Hotels Canceled: " + nHotelscanceled);
    }

    /**
     * Initiates a booking for a hotel and flight.
     *
     * @param hotel  The hotel identifier.
     * @param flight The flight identifier.
     * @param rooms  The number of rooms to book.
     * @param seats  The number of flight seats to book.
     */
    public static void book(String hotel, String flight, int rooms, int seats) {
        nTransactions++;

        String hotelService = HotelToServiceMap.get(hotel);
        String flightService = FlightToServiceMap.get(flight);
        System.out.println("Incoming-Booking: ");
        System.out.println("Hotel: " + hotel + " Flight: " + flight + " Rooms: " + rooms + " Seats: " + seats);
        if (hotelService == null || flightService == null) {
            System.out.println("Hotel or Flight not found");
            return;
        }
        HotelBooking hotelBooking = new HotelBooking(hotel, rooms);
        FlightBooking flightBooking = new FlightBooking(flight, seats);
        ComboBooking comboBooking = new ComboBooking(hotelBooking, flightBooking);

        Message hotelMessage = new Message(name, hotelService, hotelBooking);
        comboBooking.TransactionID = hotelMessage.getTransactionId();
        UUIDtoBookingMap.put(comboBooking.TransactionID, comboBooking);
        sendToMessageBroker(hotelMessage);
    }

    /**
     * Sends a message to the message broker.
     *
     * @param message The message to be sent.
     */
    static void sendToMessageBroker(Message message) {
        MessageBroker.send(message);
    }
}

/**
 * Represents a combination of hotel and flight bookings.
 */
class ComboBooking {
    HotelBooking hotelBooking;
    FlightBooking flightBooking;

    String TransactionID;

    Status status = Status.HotelPending;

    /**
     * Constructs a ComboBooking with specified hotel and flight bookings.
     *
     * @param hotelBooking  The hotel booking part of the combo.
     * @param flightBooking The flight booking part of the combo.
     */
    public ComboBooking(HotelBooking hotelBooking, FlightBooking flightBooking) {
        this.hotelBooking = hotelBooking;
        this.flightBooking = flightBooking;
    }

    /**
     * Updates the status of the hotel booking based on the answer received.
     *
     * @param success Indicates whether the booking was successful.
     */
    public void HotelAnswer(boolean success) {
        if (success) {
            status = Status.HotelConfirmed;
        } else {
            status = Status.HotelCanceled;
        }
    }

    /**
     * Updates the status of the flight booking based on the answer received.
     *
     * @param success Indicates whether the booking was successful.
     */
    public void FlightAnswer(boolean success) {
        if (success) {
            status = Status.FlightConfirmed;
        } else {
            status = Status.FlightCanceled;
        }
    }
}