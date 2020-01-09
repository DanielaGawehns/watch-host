package nl.liacs.watch.protocol.types;

import java.nio.ByteBuffer;

import org.jetbrains.annotations.NotNull;

public class MessageParameterLong extends MessageParameter {
    /**
     * Create a long parameter with the given value.
     *
     * @param val The value to use.
     */
    public MessageParameterLong(long val) {
        var bb = ByteBuffer.allocate(Long.BYTES);
        bb.putLong(val);
        this.bytes = bb.array();
    }

    /**
     * @param bytes Create a new instance with the given bytes.
     */
    MessageParameterLong(@NotNull byte[] bytes) {
        this.bytes = bytes;
    }

    /**
     * @return The parameter value as a long.
     */
    @NotNull
    @Override
    public Long getValue() {
        return ByteBuffer.wrap(bytes).getLong();
    }

    @Override
    public ParameterType getType() {
        return ParameterType.LONG;
    }
}
