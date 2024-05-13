package MessageBroker;

// Import necessary classes
import java.util.List;
import java.util.UUID;

import java.time.LocalDateTime;

// Define an interface for content
interface MessageContent {
}

// Message class that holds the unique transaction ID, content, recipients, and sender
public class Message {
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
   public Message(String transactionId, String sender, String recipient, MessageContent content) {
       this.transactionId = transactionId;
       this.sender = sender;
       this.recipient = recipient;
       this.content = content;
       this.timestamp = LocalDateTime.now(); // Gets the current time
   }

    // Getters
    public String getTransactionId() {
        return transactionId;
    }

    public String getRecipient() {
        return recipient;
    }

    public String getSender() {
        return sender;
    }

    public MessageContent getContent() {
        return content;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }



}


