package MessageBroker;

// Concrete class for FlightCancel
public class FlightCancel implements MessageContent {
   private String flightNumber;

    public String getFlightNumber() {
        return flightNumber;
    }

   // Constructor
   public FlightCancel(String flightNumber) {
      this.flightNumber = flightNumber;
   }
}
