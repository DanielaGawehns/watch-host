package nl.liacs.watch.protocol.types;

import java.nio.ByteBuffer;

import com.google.common.primitives.Doubles;

import org.jetbrains.annotations.NotNull;

public class MessageParameterDouble extends MessageParameter {
    /**
     * @param val Create a double parameter with the given value.
     */
    public MessageParameterDouble(double val) {
        var bb = ByteBuffer.allocate(Doubles.BYTES);
        bb.putDouble(val);
        this.bytes = bb.array();
    }

    /**
     * @param bytes Create a new instance with the given bytes.
     */
    MessageParameterDouble(@NotNull byte[] bytes) {
        this.bytes = bytes;
    }

    /**
     * @return The parameter value as a double.
     */
    @NotNull
    public Double getValue() {
        return ByteBuffer.wrap(bytes).getDouble();
    }

    @Override
    public ParameterType getType() {
        return ParameterType.DOUBLE;
    }
}
