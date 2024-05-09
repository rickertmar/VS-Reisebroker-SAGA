package test;

public class ConfirmationMessage {
    private int requestId;
    private boolean success;

    public ConfirmationMessage(int requestId, boolean success) {
        this.requestId = requestId;
        this.success = success;
    }

    public int getRequestId() {
        return requestId;
    }

    public boolean isSuccess() {
        return success;
    }
}

