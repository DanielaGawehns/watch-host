package nl.liacs.watch.protocol.types;

import com.google.common.io.ByteStreams;
import com.google.common.primitives.Doubles;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class MessageParameter {
    /**
     * The type of the parameter, defaults to unknown.
     * Should be set before using.
     */
    ParameterType type = ParameterType.UNKNOWN;
    /**
     * The value of the parameter.
     */
    byte[] bytes;

    MessageParameter() {
    }

    /**
     * @param bytes Create a byte array parameter with the given value.
     */
    public MessageParameter(byte[] bytes) {
        this.type = ParameterType.BINARY;
        this.bytes = bytes;
    }

    /**
     * @param string Create a string parameter with the given value.
     */
    public MessageParameter(String string) {
        this.type = ParameterType.STRING;

        var bb = ByteBuffer.allocate(string.length());
        bb.put(string.getBytes());
        this.bytes = bb.array();
    }

    /**
     * @param val Create a double parameter with the given value.
     */
    public MessageParameter(double val) {
        this.type = ParameterType.DOUBLE;

        var bb = ByteBuffer.allocate(Doubles.BYTES);
        bb.putDouble(val);
        this.bytes = bb.array();
    }

    /**
     * @return The parameter value as a string.
     * @throws IllegalStateException Throws when the parameter is not a string.
     */
    public String getString() throws IllegalStateException {
        if (type != ParameterType.STRING) {
            throw new IllegalArgumentException("type is not string");
        }
        return new String(bytes, StandardCharsets.US_ASCII);
    }

    /**
     * @return The parameter value as a double.
     * @throws IllegalStateException Throws when the parameter is not a double.
     */
    public Double getDouble() throws IllegalStateException {
        if (type != ParameterType.DOUBLE) {
            throw new IllegalArgumentException("type is not double");
        }
        return ByteStreams.newDataInput(bytes).readDouble(); // TODO: something more efficient
    }

    /**
     * @return The parameter value as a byte array.
     * @throws IllegalStateException Throws when the parameter is not a byte array.
     */
    public byte[] getBinary() throws IllegalStateException {
        if (type != ParameterType.BINARY) {
            throw new IllegalArgumentException("type is not binary");
        }
        return bytes;
    }

    /**
     * Set the type of this parameter.
     * @param type The type to set to.
     */
    public void setType(ParameterType type) {
        this.type = type;
    }

    /**
     * @return The current parameter encoded as a byte array.
     * @throws IllegalStateException When the type of the parameter is unknown.
     */
    public byte[] encode() throws IllegalStateException {
        if (this.type == ParameterType.UNKNOWN) {
            throw new IllegalStateException("parameter type can't be unknown");
        }

        int length = this.bytes.length;
        var bb = ByteBuffer.allocate(2 + length);
        bb.putShort((short) length);
        bb.put(this.bytes);
        return bb.array();
    }
}
