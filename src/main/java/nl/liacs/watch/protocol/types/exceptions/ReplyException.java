package nl.liacs.watch.protocol.types.exceptions;

/**
 * Exception when the result of a sent message is non-positive.
 */
public class ReplyException extends Exception {
    private final int statusCode;
    private final String statusMessage;

    /**
     * Create a new {@link ReplyException} with the given status code and
     * message.
     *
     * @param statusCode The status code to use.
     * @param message The status message to use.
     */
    public ReplyException(int statusCode, String message) {
        this.statusCode = statusCode;
        this.statusMessage = message;
    }

    /**
     * @return The status code of the error.
     */
    public int getStatusCode() {
        return statusCode;
    }
    /**
     * @return The status message of the error.
     */
    public String getStatusMessage() {
        return statusMessage;
    }

    @Override
    public String getMessage() {
        return String.format("(%d): %s", getStatusCode(), getStatusMessage());
    }
}
