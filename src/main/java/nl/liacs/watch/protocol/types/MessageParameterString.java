package nl.liacs.watch.protocol.types;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class MessageParameterString extends MessageParameter {
    /**
     * @param string Create a string parameter with the given value.
     */
    public MessageParameterString(String string) {
        if (string == null) {
            string = "";
        }

        var bb = ByteBuffer.allocate(string.length());
        bb.put(string.getBytes());
        this.bytes = bb.array();
    }

    /**
     * @param bytes Create a new instance with the given bytes.
     */
    MessageParameterString(byte[] bytes) {
        this.bytes = bytes;
    }

    /**
     * @return The parameter value as a string.
     */
    public String getValue() {
        return new String(bytes, StandardCharsets.US_ASCII);
    }

    @Override
    public ParameterType getType() {
        return ParameterType.STRING;
    }
}
