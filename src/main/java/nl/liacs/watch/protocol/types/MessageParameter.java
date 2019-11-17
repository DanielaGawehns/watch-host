package nl.liacs.watch.protocol.types;

import com.google.common.io.ByteStreams;
import com.google.common.primitives.Doubles;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class MessageParameter {
    ParameterType type = ParameterType.UNKNOWN;
    byte[] bytes;

    MessageParameter() {
    }

    public MessageParameter(byte[] bytes) {
        this.type = ParameterType.BINARY;
        this.bytes = bytes;
    }

    public MessageParameter(String string) {
        this.type = ParameterType.STRING;

        var bb = ByteBuffer.allocate(string.length());
        bb.put(string.getBytes());
        this.bytes = bb.array();
    }

    public MessageParameter(double val) {
        this.type = ParameterType.DOUBLE;

        var bb = ByteBuffer.allocate(Doubles.BYTES);
        bb.putDouble(val);
        this.bytes = bb.array();
    }

    public String getString() {
        if (type != ParameterType.STRING) {
            throw new IllegalArgumentException("type is not string");
        }
        return new String(bytes, StandardCharsets.US_ASCII);
    }

    public Double getDouble() {
        if (type != ParameterType.DOUBLE) {
            throw new IllegalArgumentException("type is not double");
        }
        return ByteStreams.newDataInput(bytes).readDouble(); // TODO: something more efficient
    }

    public byte[] getBinary() {
        if (type != ParameterType.BINARY) {
            throw new IllegalArgumentException("type is not binary");
        }
        return bytes;
    }

    public void setType(ParameterType type) {
        this.type = type;
    }

    public byte[] encode() throws Exception {
        if (this.type == ParameterType.UNKNOWN) {
            throw new Exception("parameter type can't be unknown");
        }

        int length = this.bytes.length;
        var bb = ByteBuffer.allocate(2 + length);
        bb.putShort((short) length);
        bb.put(this.bytes);
        return bb.array();
    }
}
