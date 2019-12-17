package nl.liacs.watch.protocol.types.exceptions;

/**
 * Exception when the result of a sent message is non-positive.
 */
public class ReplyException extends Exception {
    private final int statusCode;
    private final String statusMessage;

    public ReplyException(int statusCode, String message) {
        this.statusCode = statusCode;
        this.statusMessage = message;
    }

    public int getStatusCode() {
        return statusCode;
    }
    public String getStatusMessage() {
        return statusMessage;
    }

    @Override
    public String getMessage() {
        return String.format("(%d): %s", getStatusCode(), getStatusMessage());
    }
}
