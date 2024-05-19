package MessageBroker;

// Concrete class for FlightCancel
public class FlightCancel implements MessageContent {
    public String getType() {
        return "FlightCancel";
    }

    private final String flightNumber;
    private final int noSeats;

    public final String getFlightNumber() {
        return flightNumber;
    }

    public final int getNoSeats() {
        return noSeats;
    }

    public FlightCancel(String flightNumber, int noSeats) {
        this.flightNumber = flightNumber;
        this.noSeats = noSeats;
    }
}
