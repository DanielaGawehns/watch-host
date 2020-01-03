package nl.liacs.watch.protocol.types;

import org.jetbrains.annotations.NotNull;

public class MessageParameterBinary extends MessageParameter {
    /**
     * @param bytes Create a byte array parameter with the given value.
     */
    public MessageParameterBinary(@NotNull byte[] bytes) {
        this.bytes = bytes;
    }

    /**
     * @return The parameter value as a byte array.
     */
    @NotNull
    public byte[] getValue() {
        return bytes;
    }

    @Override
    public ParameterType getType() {
        return ParameterType.BINARY;
    }
}
