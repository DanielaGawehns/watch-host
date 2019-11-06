package nl.liacs.watch.protocol.types;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import java.io.DataInputStream;
import java.io.IOException;

public class Message {
    public MessageType type;
    public MessageParameter[] parameters;

    Message() {
    }

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

    public byte[] encode() throws Exception {
        ByteArrayDataOutput bb = ByteStreams.newDataOutput();

        bb.write(type.ordinal());
        bb.write(parameters.length);

        for (MessageParameter parameter : parameters) {
            bb.write(parameter.encode());
        }

        return bb.toByteArray();
    }
}
