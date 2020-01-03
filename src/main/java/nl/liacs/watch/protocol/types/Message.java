package nl.liacs.watch.protocol.types;

import java.io.DataInputStream;
import java.io.IOException;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import org.jetbrains.annotations.NotNull;

/**
 * Class representing a protocol message.
 */
public class Message {
    /**
     * The message ID
     */
    public int id = 0;
    /**
     * The type of the message.
     */
    public final MessageType type;
    /**
     * Every parameter of the message.
     */
    public MessageParameter[] parameters = new MessageParameter[0];

    /**
     * Create a new {@link Message} with the given type.
     *
     * @param type The type for the message to have.
     */
    public Message(MessageType type) {
        this.type = type;
    }

    /**
     * @param s The data input stream to read from.
     * @return The parsed message.
     * @throws IOException IO error when reading from the stream failed.
     */
    @NotNull
    public static Message decode(DataInputStream s) throws IOException {
        var id = s.readUnsignedShort();
        var type = MessageType.values()[s.readUnsignedByte()];

        var msg = new Message(type);
        msg.id = id;

        msg.parameters = new MessageParameter[s.readUnsignedByte()];
        for (int i = 0; i < msg.parameters.length; i++) {
            int psize = s.readUnsignedShort();

            byte[] pbytes = new byte[psize];
            s.readFully(pbytes, 0, psize);

            var param = new MessageParameter();
            param.bytes = pbytes;
            msg.parameters[i] = param;
        }

        return msg;
    }

    /**
     * @return The current message encoded as a byte array.
     * @throws IllegalStateException When the type of a parameter is unknown.
     */
    public byte[] encode() throws IllegalStateException {
        ByteArrayDataOutput bb = ByteStreams.newDataOutput();

        bb.writeShort(this.id);
        bb.writeByte(type.getId());
        bb.writeByte(parameters.length);

        for (MessageParameter parameter : parameters) {
            bb.write(parameter.encode());
        }

        return bb.toByteArray();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj == this) return true;
        if (!Message.class.isAssignableFrom(obj.getClass())) return false;

        final Message other = (Message) obj;
        if (other.parameters.length != this.parameters.length) {
            return false;
        }
        for (int i = 0; i < this.parameters.length; i++) {
            if (!other.parameters[i].equals(this.parameters[i])) {
                return false;
            }
        }
        return other.type == this.type;
    }

    /**
     * Create a new reply for this message.
     *
     * @param statusCode The status code to use.
     * @param message The status message to use, required when
     * {@code statusCode} is non-null.
     * @param parameters The results to send.
     * @return The newly constructed message.
     */
    @NotNull
    public Message makeReply(
        int statusCode,
        String message,
        MessageParameter... parameters
    ) {
        return Message.makeReply(this.id, statusCode, message, parameters);
    }

    /**
     * Create a new reply to the message with the given {@code messageID}.
     *
     * @param messageID The ID of the message to reply to.
     * @param statusCode The status code to use.
     * @param message The status message to use, required when
     * {@code statusCode} is not 0.
     * @param parameters The results to send.
     * @return The newly constructed message.
     * @throws IllegalArgumentException Illegal argument error when message is
     * null or empty when statusCode is not 0.
     */
    @NotNull
    private static Message makeReply(
        int messageID,
        int statusCode,
        String message,
        MessageParameter... parameters
    ) throws IllegalArgumentException {
        var res = new Message(MessageType.REPLY);
        res.id = messageID;

        if (statusCode != 0 && Strings.isNullOrEmpty(message)) {
            throw new IllegalArgumentException("message must be given when statusCode is not 0");
        }

        res.parameters = new MessageParameter[2 + parameters.length];
        res.parameters[0] = new MessageParameterDouble(statusCode);
        res.parameters[1] = new MessageParameterString(message);
        System.arraycopy(parameters, 0, res.parameters, 2, parameters.length);

        return res;
    }

    @Override
    public String toString() {
        var paramSb = new StringBuilder();
        for (int i = 0; i < parameters.length; i++) {
            var param = parameters[i];
            if (i > 0) {
                paramSb.append(", ");
            }

            paramSb.append(param.toString());
        }

        return String.format("Message{ %d, %s, %s }", id, type.name(), paramSb.toString());
    }
}
