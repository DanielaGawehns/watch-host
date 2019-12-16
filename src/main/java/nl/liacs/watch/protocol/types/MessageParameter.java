package nl.liacs.watch.protocol.types;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class MessageParameter {
    /**
     * The value of the parameter.
     */
    byte[] bytes;

    MessageParameter() {
    }

    /**
     * Create a derived class with the string type.
     */
    public MessageParameterString asString() {
        return new MessageParameterString(this.bytes);
    }

    /**
     * Create a derived class with the double type.
     */
    public MessageParameterDouble asDouble() {
        return new MessageParameterDouble(this.bytes);
    }

    /**
     * Create a derived class with the binary type.
     */
    public MessageParameterBinary asBinary() {
        return new MessageParameterBinary(this.bytes);
    }

    /**
     * @return The current parameter encoded as a byte array.
     * @throws IllegalStateException When the type of the parameter is unknown.
     */
    public byte[] encode() throws IllegalStateException {
        if (this.getType() == ParameterType.UNKNOWN) {
            throw new IllegalStateException("parameter type can't be unknown");
        }

        int length = this.bytes.length;
        var bb = ByteBuffer.allocate(2 + length);
        bb.putShort((short) length);
        bb.put(this.bytes);
        return bb.array();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj == this) return true;
        if (!MessageParameter.class.isAssignableFrom(obj.getClass())) return false;

        final MessageParameter other = (MessageParameter) obj;
        return other.getType() == this.getType() && Arrays.equals(other.bytes, this.bytes);
    }

    @Override
    public String toString() {
        var type = this.getType();

        var sb = new StringBuilder(type.name());

        switch (type) {
        case DOUBLE:
        case STRING:
            sb.append('(');
            if (type == ParameterType.DOUBLE) {
                sb.append(((MessageParameterDouble) this).getValue());
            } else {
                sb.append(((MessageParameterString) this).getValue());
            }
            sb.append(')');
            break;

        default:
            break;
        }

        return sb.toString();
    }

    public ParameterType getType() {
        return ParameterType.UNKNOWN;
    }
}
