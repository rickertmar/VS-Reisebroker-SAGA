package MessageBroker;

public class ComboBooking implements MessageContent {
   private final FlightBooking flightBooking;
   private final HotelBooking hotelBooking;

    public FlightBooking getFlightBooking() {
        return flightBooking;
    }

    public HotelBooking getHotelBooking() {
        return hotelBooking;
    }

   // Constructor
   public ComboBooking(FlightBooking flightBooking, HotelBooking hotelBooking) {
      this.flightBooking = flightBooking;
      this.hotelBooking = hotelBooking;
   }
}
