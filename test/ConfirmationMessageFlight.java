package test;

public class ConfirmationMessageFlight {
    private int requestId;
    private boolean success;

    public ConfirmationMessageFlight(int requestId, boolean success) {
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

