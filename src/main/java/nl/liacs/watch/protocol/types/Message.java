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

    Message() {
    }

    /**
     * @param s The data input stream to read from.
     * @return The parsed message.
     * @throws IOException IO error when reading from the stream failed.
     */
    @org.jetbrains.annotations.NotNull
    public static Message decode(DataInputStream s) throws IOException {
        var msg = new Message();
        msg.type = MessageType.values()[s.readUnsignedByte()];

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
}
