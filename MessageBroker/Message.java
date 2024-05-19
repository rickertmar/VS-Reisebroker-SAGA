package MessageBroker;

import java.util.Random;
import java.time.LocalDateTime;

// Message class that holds the unique transaction ID, content, recipients, and sender
public class Message {
    private static final Random random = new Random();
    private static int transactionCounter = 0;

    private final String transactionId;
    private final String recipient;
    private final String sender;
    private final MessageContent content;
    private final LocalDateTime timestamp;

    // Constructor for new messages
    public Message(String sender, String recipient, MessageContent content) {
        this.transactionId = generateTransactionId(); // Generates a unique ID
        this.sender = sender;
        this.recipient = recipient;
        this.content = content;
        this.timestamp = LocalDateTime.now(); // Gets the current time
    }

    // Constructor for messages with existing transaction IDs
    public Message(String transactionId, String sender, String recipient, MessageContent content) {
        this.transactionId = transactionId;
        this.sender = sender;
        this.recipient = recipient;
        this.content = content;
        this.timestamp = LocalDateTime.now();
    }

    // Synchronized method to generate unique transaction IDs
    private synchronized static String generateTransactionId() {
        transactionCounter++;
        return "T" + transactionCounter;
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
