package nl.liacs.watch.protocol.types;

import java.nio.ByteBuffer;

import org.jetbrains.annotations.NotNull;

public class MessageParameterInteger extends MessageParameter {
    /**
     * Create a integer parameter with the given value.
     *
     * @param val The value to use.
     */
    public MessageParameterInteger(int val) {
        var bb = ByteBuffer.allocate(Integer.BYTES);
        bb.putInt(val);
        this.bytes = bb.array();
    }

    /**
     * @param bytes Create a new instance with the given bytes.
     */
    MessageParameterInteger(@NotNull byte[] bytes) {
        this.bytes = bytes;
    }

    /**
     * @return The parameter value as an integer.
     */
    @NotNull
    @Override
    public Integer getValue() {
        return ByteBuffer.wrap(bytes).getInt();
    }

    @Override
    public ParameterType getType() {
        return ParameterType.INTEGER;
    }
}
