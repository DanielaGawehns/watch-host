package nl.liacs.watch.protocol.types;

import org.jetbrains.annotations.NotNull;

public class MessageParameterBinary extends MessageParameter {
    /**
     * Create a byte array parameter with the given value.
     *
     * @param bytes The value to use.
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
