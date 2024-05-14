package MessageBroker;

// Concrete class for HotelBooking
public class HotelBooking implements MessageContent {
   private final String hotelName;
   private final int numberOfRooms;

    public String getHotelName() {
        return hotelName;
    }

    public int getNumberOfRooms() {
        return numberOfRooms;
    }

   // Constructor
   public HotelBooking(String hotelName, int numberOfRooms) {
      this.hotelName = hotelName;
      this.numberOfRooms = numberOfRooms;
   }

}
