package MessageBroker;

// Concrete class for HotelCancel
public class HotelCancel implements MessageContent {
    public String getType() {
        return "HotelCancel";
    }

    private final int noRooms;

    public int getNoRooms() {
        return noRooms;
    }

    private final String hotelName;

    public String getHotelName() {
        return hotelName;
    }

    public HotelCancel(String hotelName, int noRooms) {
        this.hotelName = hotelName;
        this.noRooms = noRooms;
    }
}
