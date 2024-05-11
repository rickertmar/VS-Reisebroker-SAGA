package MessageBroker;

// Concrete class for Answer
public class Answer implements MessageContent {
   private boolean success;

        public boolean isSuccess() {
            return success;
        }
   // Constructor
   public Answer(boolean success) {
      this.success = success;
   }

}
