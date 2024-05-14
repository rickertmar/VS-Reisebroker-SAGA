package MessageBroker;

// Concrete class for HotelCancel
public class HotelCancel implements MessageContent {
   private final String hotelName;

   public String getHotelName() {
      return hotelName;
   }

   // Constructor
   public HotelCancel(String hotelName) {
      this.hotelName = hotelName;
   }
}
