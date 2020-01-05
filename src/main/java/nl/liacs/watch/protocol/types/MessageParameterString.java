package nl.liacs.watch.protocol.types;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MessageParameterString extends MessageParameter {
    /**
     * Create a string parameter with the given value.
     *
     * @param string The string to use.
     */
    public MessageParameterString(@Nullable String string) {
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
    MessageParameterString(@NotNull byte[] bytes) {
        this.bytes = bytes;
    }

    /**
     * @return The parameter value as a string.
     */
    @NotNull
    public String getValue() {
        return new String(bytes, StandardCharsets.US_ASCII);
    }

    @Override
    public ParameterType getType() {
        return ParameterType.STRING;
    }
}
