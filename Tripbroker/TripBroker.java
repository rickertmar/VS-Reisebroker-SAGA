package Tripbroker;

import java.util.*;

import MessageBroker.Message;

import MessageBroker.MessageContent;

import MessageBroker.HotelBooking;

import MessageBroker.FlightBooking;

import MessageBroker.Answer;

import MessageBroker.FlightCancel;

import MessageBroker.HotelCancel;

import java.time.LocalDateTime;

import MessageBroker.MessageBroker;

public class TripBroker {

    public static final String name = "TripBroker";

    private static MessageBroker messageBroker;

    private static Map<String,ComboBooking> UUIDtoBookingMap = new HashMap<String,ComboBooking>();




    private static Map<String,String> HotelToServiceMap = new HashMap<String,String>();
    private static Map<String,String> FlightToServiceMap = new HashMap<String,String>();

    public static void addFlights(String[] flights, String service){
        for(String flight:flights){
            FlightToServiceMap.put(flight,service);
        }
    }

    public static void addHotels(String[] hotels, String service){
        for(String hotel:hotels){
            HotelToServiceMap.put(hotel,service);
        }
    }
    public  static void setMessageBroker(MessageBroker messageBroker){
        TripBroker.messageBroker = messageBroker;
    }

    public static String[] getHotels(){
        return HotelToServiceMap.keySet().toArray(new String[0]);
    }

    public static void receiveMessage(Message message){
        String transactionId = message.getTransactionId();
        ComboBooking comboBooking = UUIDtoBookingMap.get(transactionId);
        if(comboBooking == null){
            System.out.println("Transaction not found");
            return;
        }
        if(message.getContent().getType().equals("Answer")) {
            Answer answer = (Answer) message.getContent();
            Status status = comboBooking.status;
            switch (status){
                case HotelPending:
                    if(answer.isSuccess()){
                        comboBooking.HotelAnswer(true);
                        Message flightMessage = new Message(comboBooking.FlightTransactionID,name,FlightToServiceMap.get(comboBooking.flightBooking.getFlightNumber()),comboBooking.flightBooking);
                        sendToMessageBroker(flightMessage);
                    }else{
                        comboBooking.HotelAnswer(false);
                        Message cancelMessage = new Message(comboBooking.HotelTransactionID,name,HotelToServiceMap.get(comboBooking.hotelBooking.getHotelName()),new HotelCancel(comboBooking.hotelBooking.getHotelName(),comboBooking.hotelBooking.getNumberOfRooms()));
                        sendToMessageBroker(cancelMessage);
                    }
                    break;

                case FlightPending:
                    if(answer.isSuccess()){
                        comboBooking.FlightAnswer(true);
                    }else{
                        comboBooking.FlightAnswer(false);
                        Message cancelMessage = new Message(comboBooking.FlightTransactionID,name,FlightToServiceMap.get(comboBooking.flightBooking.getFlightNumber()),new FlightCancel(comboBooking.flightBooking.getFlightNumber(),comboBooking.flightBooking.getNumberOfSeats()));
                        sendToMessageBroker(cancelMessage);
                    }
                    break;

                default:
                    System.out.println("Invalid Status for Answer");
            }
        }

    }

    public static void book(String hotel, String flight, int rooms, int seats){
        String hotelService = HotelToServiceMap.get(hotel);
        String flightService = FlightToServiceMap.get(flight);
        if(hotelService == null || flightService == null){
            System.out.println("Hotel or Flight not found");
            return;
        }
        HotelBooking hotelBooking = new HotelBooking(hotel,rooms);
        FlightBooking flightBooking = new FlightBooking(flight,seats);
        ComboBooking comboBooking = new ComboBooking(hotelBooking,flightBooking);
        UUIDtoBookingMap.put(comboBooking.HotelTransactionID,comboBooking);
        UUIDtoBookingMap.put(comboBooking.FlightTransactionID,comboBooking);

        Message hotelMessage = new Message(comboBooking.HotelTransactionID,name,hotelService,hotelBooking);
        sendToMessageBroker(hotelMessage);
    }

    static void sendToMessageBroker(Message message){
        messageBroker.send(message);
    }






    //1 beide true  dann passt

    // einer false dann muss der der true ist rollback bzw cancel

    // beide false dann passt auch


}

class ComboBooking{
    HotelBooking hotelBooking;
    FlightBooking flightBooking;

    String FlightTransactionID;
    String HotelTransactionID;

    Status status = Status.HotelPending;

    public ComboBooking(HotelBooking hotelBooking, FlightBooking flightBooking){
        this.hotelBooking = hotelBooking;
        this.flightBooking = flightBooking;
    }

    public void HotelAnswer(boolean success){
        if(success){
            status = Status.HotelConfirmed;
        }else{
            status = Status.HotelCanceled;
        }
    }

    public void FlightAnswer(boolean success){
        if(success){
            status = Status.FlightConfirmed;
        }else{
            status = Status.FlightCanceled;
        }
    }

}
enum Status{
    HotelPending,
    HotelConfirmed,
    HotelCanceled,
    FlightPending,
    FlightConfirmed,
    FlightCanceled,

}
