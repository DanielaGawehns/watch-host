package nl.liacs.watch.protocol.tests;

import nl.liacs.watch.protocol.types.Message;
import nl.liacs.watch.protocol.types.MessageParameter;
import nl.liacs.watch.protocol.types.MessageType;
import nl.liacs.watch.protocol.types.ParameterType;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class MessageTests {
    @Test
    public void testMessageEncode() throws IOException {
        var msg = new Message();
        msg.type = MessageType.INCREMENT;
        msg.parameters = new MessageParameter[5];
        msg.parameters[0] = new MessageParameter("accel");
        msg.parameters[1] = new MessageParameter(100);
        msg.parameters[2] = new MessageParameter(5.43894);
        msg.parameters[3] = new MessageParameter(3.47392);
        msg.parameters[4] = new MessageParameter(1.32419);

        var encoded = msg.encode();
        var decoded = Message.decode(new DataInputStream(new ByteArrayInputStream(encoded)));

        decoded.parameters[0].setType(ParameterType.STRING);
        decoded.parameters[1].setType(ParameterType.DOUBLE);
        decoded.parameters[2].setType(ParameterType.DOUBLE);
        decoded.parameters[3].setType(ParameterType.DOUBLE);
        decoded.parameters[4].setType(ParameterType.DOUBLE);

        Assert.assertTrue(msg.equals(decoded));
    }
}
