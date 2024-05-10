package MessageBroker;

// Import necessary classes
import java.util.List;
import java.util.UUID;

import java.time.LocalDateTime;

// Define an interface for content
interface MessageContent {
}

class ComboBooking implements MessageContent {
   private FlightBooking flightBooking;
   private HotelBooking hotelBooking;

   // Constructor
   public ComboBooking(FlightBooking flightBooking, HotelBooking hotelBooking) {
      this.flightBooking = flightBooking;
      this.hotelBooking = hotelBooking;
   }
}

// Concrete class for HotelBooking
class HotelBooking implements MessageContent {
   private String hotelName;
   private int numberOfRooms;

   // Constructor
   public HotelBooking(String hotelName, int numberOfRooms) {
      this.hotelName = hotelName;
      this.numberOfRooms = numberOfRooms;
   }

}

// Concrete class for FlightBooking
class FlightBooking implements MessageContent {
   private String flightNumber;
   private int numberOfSeats;

   // Constructor
   public FlightBooking(String flightNumber, int numberOfSeats) {
      this.flightNumber = flightNumber;
      this.numberOfSeats = numberOfSeats;
   }
}

// Concrete class for FlightCancel
class FlightCancel implements MessageContent {
   private String flightNumber;

   // Constructor
   public FlightCancel(String flightNumber) {
      this.flightNumber = flightNumber;
   }
}

// Concrete class for HotelCancel
class HotelCancel implements MessageContent {
   private String hotelName;

   // Constructor
   public HotelCancel(String hotelName) {
      this.hotelName = hotelName;
   }
}

// Concrete class for Answer
class Answer implements MessageContent {
   private boolean success;

   // Constructor
   public Answer(boolean success) {
      this.success = success;
   }

}

// Message class that holds the unique transaction ID, content, recipients, and sender
final class Message {
   private String transactionId;
   private String recipient;
   private String sender;
   private MessageContent content;
   private LocalDateTime timestamp;
   // Constructor
   public Message(String sender, String recipient, MessageContent content) {
      this.transactionId = UUID.randomUUID().toString(); // Generates a unique ID
      this.sender = sender;
      this.recipient = recipient;
      this.content = content;
      this.timestamp = LocalDateTime.now(); // Gets the current time
   }

}


