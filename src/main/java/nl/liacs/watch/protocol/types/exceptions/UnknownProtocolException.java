package nl.liacs.watch.protocol.types.exceptions;

/**
 * Exception when the protocol (version) is unknown
 */
public class UnknownProtocolException extends Exception {
    private final int protocolVersion;

    public UnknownProtocolException(String message, int protocolVersion) {
        super(message);
        this.protocolVersion = protocolVersion;
    }

    public int getProtocolVersion() {
        return protocolVersion;
    }

    @Override
    public String getMessage() {
        return super.getMessage() + " (protocol version " + this.protocolVersion + ")";
    }
}
