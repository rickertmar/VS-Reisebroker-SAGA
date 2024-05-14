package MessageBroker;

// Concrete class for FlightCancel
public class FlightCancel implements MessageContent {
   private final String flightNumber;

    public final String getFlightNumber() {
        return flightNumber;
    }

   // Constructor
   public FlightCancel(String flightNumber) {
      this.flightNumber = flightNumber;
   }
}
