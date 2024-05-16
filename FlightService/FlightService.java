package FlightService;

import MessageBroker.MessageBroker;
import MessageBroker.Message;
import MessageBroker.Answer;
import MessageBroker.FlightBooking;
import MessageBroker.FlightCancel;

import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CompletableFuture;

import DataFiller.FillProperties;

public class FlightService {
    public String name;
    private Flight[] flights;
    private static Random random = new Random();

    private ConcurrentHashMap<String, CompletableFuture<Message>> Answers = new ConcurrentHashMap<>();

    public FlightService(String name, Flight[] flights) {
        this.name = name;
        this.flights = flights;
    }

    public String getName() {
        return name;
    }

    public String[] getFlights() {
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

    public void receiveMessage(Message requestMessage) {
        int chanceToNotDoAnything = FillProperties.getChanceToNotDoAnything();
        if (random.nextInt(100) < chanceToNotDoAnything) {
            return;
        }

        String transactionId = requestMessage.getTransactionId();
        if (transactionId == null) {
            System.err.println("Error: Transaction ID is null in receiveMessage");
            return;
        }
        CompletableFuture<Message> cachedAnswer = Answers.get(transactionId);

        if (cachedAnswer != null) {
            cachedAnswer.thenAccept(MessageBroker::send);
        } else {
            CompletableFuture<Message> newAnswer = CompletableFuture.supplyAsync(() -> processMessage(requestMessage));
            Answers.put(transactionId, newAnswer);

            int chanceToNotSendMessage = FillProperties.getChanceToNotSendMessage();
            if (random.nextInt(100) < chanceToNotSendMessage) {
                return;
            }

            newAnswer.thenAccept(MessageBroker::send);
        }
    }


    private Message processMessage(Message requestMessage) {
        String transactionId = requestMessage.getTransactionId();
        switch (requestMessage.getContent().getType()) {
            case "FlightBooking":
                FlightBooking flightBooking = (FlightBooking) requestMessage.getContent();
                Flight flight = findFlight(flightBooking.getFlightNumber());
                boolean success = flight.bookSeats(flightBooking.getNumberOfSeats());
                return new Message(transactionId, this.name, requestMessage.getSender(), new Answer(success));
            case "FlightCancel":
                FlightCancel flightCancel = (FlightCancel) requestMessage.getContent();
                Flight flight1 = findFlight(flightCancel.getFlightNumber());
                flight1.releaseSeats(flightCancel.getNoSeats());
                return new Message(transactionId, this.name, requestMessage.getSender(), new Answer(true));
            default:
                return new Message(transactionId, this.name, requestMessage.getSender(), new Answer(false));
        }
    }

    private void sendFromCache(String transactionId) throws InterruptedException {
        CompletableFuture<Message> answer = Answers.get(transactionId);
        if (answer != null) {
            answer.thenAccept(MessageBroker::send);
        } else {
            throw new InterruptedException();
        }
    }
}
