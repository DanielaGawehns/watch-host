package nl.liacs.watch.protocol.types;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

public class Message {
    public MessageType type;
    public MessageParameter[] parameters;

    @org.jetbrains.annotations.NotNull
    public static Message decode(byte[] bytes) {
        ByteArrayDataInput s = ByteStreams.newDataInput(bytes);

        MessageType mtype = MessageType.values()[s.readUnsignedByte()];
        int nparams = s.readUnsignedByte();

        MessageParameter[] params = new MessageParameter[nparams];
        for (int i = 0; i < nparams; i++) {
            int psize = s.readUnsignedShort();

            byte[] pbytes = new byte[psize];
            s.readFully(pbytes, 0, psize);

            var param = new MessageParameter();
            param.type = ParameterType.UNKNOWN;
            param.bytes = pbytes;
            params[i] = param;
        }

        var msg = new Message();
        msg.type = mtype;
        msg.parameters = params;
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
