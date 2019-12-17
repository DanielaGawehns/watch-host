package nl.liacs.watch.protocol.types;

import com.google.common.primitives.Doubles;
import java.nio.ByteBuffer;

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
    MessageParameterDouble(byte[] bytes) {
        this.bytes = bytes;
    }

    /**
     * @return The parameter value as a double.
     */
    public Double getValue() {
        return ByteBuffer.wrap(bytes).getDouble();
    }

    @Override
    public ParameterType getType() {
        return ParameterType.DOUBLE;
    }
}
