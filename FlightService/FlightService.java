package FlightService;

import MessageBroker.MessageBroker;
import MessageBroker.Message;

import MessageBroker.Answer;
import MessageBroker.FlightBooking;
import MessageBroker.FlightCancel;
import java.util.Random;
import java.util.HashMap;
import java.util.Map;
//CompletableFuture
import java.util.concurrent.CompletableFuture;

import DataFiller.FillProperties;

public class FlightService {
    public String name;
    private Flight[] flights;
    private static Random random = new Random();

    private Map<String, CompletableFuture<Message>> Answers = new HashMap<String, CompletableFuture<Message>>();

    public String getName() {
        return name;
    }
    public String[] getflights() {
        String[] flightNames = new String[flights.length];
        for (int i = 0; i < flights.length; i++) {
            flightNames[i] = flights[i].name;
        }
        return flightNames;
    }

    private Flight findFlight(String flightName) {
        for (Flight flight : flights) {
            if (flight.name.equals(flightName)) {
                return flight;
            }
        }
        return null;
    }

    public FlightService(String name, Flight[] flights){
        this.name = name;
        this.flights = flights;
    }

    private Random randomNumber = new Random();
    public void receiveMessage(Message RequestMessage) {
        //todo random chance to not do anything HERE
        if (randomNumber.nextInt(100) < FillProperties.getChanceToNotDoAnything()) {
            return;
        }


        String transactionId = RequestMessage.getTransactionId();
        CompletableFuture<Message> cachedAnswer = Answers.get(transactionId);

        if (cachedAnswer != null) {
            cachedAnswer.thenAccept(MessageBroker::send);
        } else {
            // Process the booking asynchronously
            CompletableFuture<Message> newAnswer = CompletableFuture.supplyAsync(() -> {
                switch (RequestMessage.getContent().getType()){
                    case "FlightBooking":
                        FlightBooking flightBooking = (FlightBooking) RequestMessage.getContent();
                        Flight flight = findFlight(flightBooking.getFlightNumber());
                        boolean success = flight.bookSeats(flightBooking.getNumberOfSeats());
                        return new Message(transactionId, this.name, RequestMessage.getSender(), new Answer(success));
                    case "FlightCancel":
                        FlightCancel flightCancel = (FlightCancel) RequestMessage.getContent();
                        Flight flight1= findFlight(flightCancel.getFlightNumber());
                        flight1.releaseSeats(flightCancel.getNoSeats());
                        return new Message(transactionId, this.name, RequestMessage.getSender(), new Answer(true));
                    default:
                        //invalid type
                        return new Message(transactionId,this.name, RequestMessage.getSender(), new Answer(false));
                }

            });
            // Cache the new CompletableFuture for future requests
            Answers.put(transactionId, newAnswer);

            // todo random chance to not send the message HERE
            if (randomNumber.nextInt(100) < FillProperties.getChanceToNotSendMessage()) {
                return;
            }

            // Once the CompletableFuture completes, send the message
            newAnswer.thenAccept(MessageBroker::send);
        }
    }



    private void sendFromCache(String transactionId)throws InterruptedException{
        CompletableFuture<Message> answer = Answers.get(transactionId);
        if (answer != null) {
            answer.thenAccept((Message message) -> MessageBroker.send(message));
        }
        else {
            throw new InterruptedException();
        }
    }
}

