package MessageBroker;

// Concrete class for HotelBooking
public class HotelBooking implements MessageContent {
    public String getType() {
        return "HotelBooking";
    }

    private final String hotelName;
    private final int numberOfRooms;

    public String getHotelName() {
        return hotelName;
    }

    public int getNumberOfRooms() {
        return numberOfRooms;
    }

    public HotelBooking(String hotelName, int numberOfRooms) {
        this.hotelName = hotelName;
        this.numberOfRooms = numberOfRooms;
    }

}
