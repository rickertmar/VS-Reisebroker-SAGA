package MessageBroker;

// Concrete class for FlightBooking
public class FlightBooking implements MessageContent {
   private final String flightNumber;
   private final int numberOfSeats;

    public String getFlightNumber() {
        return flightNumber;
    }

    public int getNumberOfSeats() {
        return numberOfSeats;
    }

   // Constructor
   public FlightBooking(String flightNumber, int numberOfSeats) {
      this.flightNumber = flightNumber;
      this.numberOfSeats = numberOfSeats;
   }
}
