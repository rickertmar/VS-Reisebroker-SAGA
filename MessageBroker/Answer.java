package MessageBroker;

// Concrete class for Answer
public class Answer implements MessageContent {
   private final boolean success;

   public boolean isSuccess() {
            return success;
        }
   // Constructor
   public Answer(boolean success) {
      this.success = success;
   }

}
