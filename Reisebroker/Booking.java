package Reisebroker;
enum SubBookingStatus {
    CREATED, SENT, CONFIRMED, CANCELLED
}


public class Booking {
    public int ID;
    public subBooking Hotel;
    public subBooking Flight;

    public SubBookingStatus getHotelStatus() {
        return Hotel.status;
    }
    public SubBookingStatus getFlightStatus() {
        return Flight.status;
    }

    public Booking(String hotelname, int roomnumber, String flightname, int flightnumber) {
        this.Hotel = new subBooking(hotelname, roomnumber);
        this.Flight = new subBooking(flightname, flightnumber);
    }
}

class subBooking {
    public int ID;
    // status for the hotel as enum sent, confirmed, cancelled
    SubBookingStatus status;
    String hotelName;
    int RoomNumber;

    public subBooking(String hotelName, int RoomNumber) {
        this.hotelName = hotelName;
        this.RoomNumber = RoomNumber;
        this.status = SubBookingStatus.CREATED;
    }
}




