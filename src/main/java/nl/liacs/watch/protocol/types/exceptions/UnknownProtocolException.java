package nl.liacs.watch.protocol.types.exceptions;

/**
 * Exception when the protocol (version) is unknown
 */
public class UnknownProtocolException extends Exception {
    private final int protocolVersion;

    /**
     * Create a new {@link UnknownProtocolException} with the given message and
     * protocol version.
     *
     * @param message The error message to use.
     * @param protocolVersion The protocol version of the other side.
     */
    public UnknownProtocolException(String message, int protocolVersion) {
        super(message);
        this.protocolVersion = protocolVersion;
    }

    /**
     * @return The protocol version of the other side.
     */
    public int getProtocolVersion() {
        return protocolVersion;
    }

    @Override
    public String getMessage() {
        return super.getMessage() + " (protocol version " + this.protocolVersion + ")";
    }
}
