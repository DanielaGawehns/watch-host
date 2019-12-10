package nl.liacs.watch.protocol.types;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import java.io.DataInputStream;
import java.io.IOException;

/**
 * Class representing a protocol message.
 */
public class Message {
    /**
     * The type of the message.
     */
    public MessageType type;
    /**
     * Every parameter of the message.
     */
    public MessageParameter[] parameters;

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
    @org.jetbrains.annotations.NotNull
    public static Message decode(DataInputStream s) throws IOException {
        var type = MessageType.values()[s.readUnsignedByte()];
        var msg = new Message(type);

        msg.parameters = new MessageParameter[s.readUnsignedByte()];
        for (int i = 0; i < msg.parameters.length; i++) {
            int psize = s.readUnsignedShort();

            byte[] pbytes = new byte[psize];
            s.readFully(pbytes, 0, psize);

            var param = new MessageParameter();
            param.type = ParameterType.UNKNOWN;
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

        bb.writeByte(type.ordinal());
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
}
