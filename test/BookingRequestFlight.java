package test;

public class BookingRequestFlight {
    private int requestId;
    private int numSeats;
    private boolean isCancelled; // Neue Variable zum Verfolgen der Stornierung

    public BookingRequestFlight(int requestId, int numSeats) {
        this.requestId = requestId;
        this.numSeats = numSeats;
        this.isCancelled = false; // Initial ist die Buchung nicht storniert
    }

    // Getter-Methoden


    public int getRequestId() {
        return requestId;
    }

    public int getNumSeats() {
        return numSeats;
    }



    public void cancelBooking() {
        this.isCancelled = true; // Setze die Buchung als storniert
    }

    public boolean isCancelled() {
        return isCancelled;
    }
}
