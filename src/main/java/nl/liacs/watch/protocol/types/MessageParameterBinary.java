package nl.liacs.watch.protocol.types;

public class MessageParameterBinary extends MessageParameter {
    /**
     * @param bytes Create a byte array parameter with the given value.
     */
    public MessageParameterBinary(byte[] bytes) {
        this.bytes = bytes;
    }

    /**
     * @return The parameter value as a byte array.
     */
    public byte[] getValue() {
        return bytes;
    }

    @Override
    public ParameterType getType() {
        return ParameterType.BINARY;
    }
}
