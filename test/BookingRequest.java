package test;

public class BookingRequest {
    private int requestId;
    private int numRooms;
    private boolean isCancelled; // Neue Variable zum Verfolgen der Stornierung

    public BookingRequest(int requestId, int numRooms) {
        this.requestId = requestId;
        this.numRooms = numRooms;
        this.isCancelled = false; // Initial ist die Buchung nicht storniert
    }

    // Getter-Methoden


    public int getRequestId() {
        return requestId;
    }

    public int getNumRooms() {
        return numRooms;
    }

    public void cancelBooking() {
        this.isCancelled = true; // Setze die Buchung als storniert
    }

    public boolean isCancelled() {
        return isCancelled;
    }
}
