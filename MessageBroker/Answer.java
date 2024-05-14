package MessageBroker;

// Concrete class for Answer
public class Answer implements MessageContent {
    public String getType() {
       return "Answer";
    }
   private final boolean success;

   public boolean isSuccess() {
            return success;
        }
   // Constructor
   public Answer(boolean success) {
      this.success = success;
   }

}
