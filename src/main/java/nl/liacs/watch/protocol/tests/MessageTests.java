package nl.liacs.watch.protocol.tests;

import nl.liacs.watch.protocol.types.*;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class MessageTests {
    @Test
    public void testMessageEncode() throws IOException {
        var msg = new Message(MessageType.INCREMENT);
        msg.parameters = new MessageParameter[5];
        msg.parameters[0] = new MessageParameterString("accel");
        msg.parameters[1] = new MessageParameterDouble(100);
        msg.parameters[2] = new MessageParameterDouble(5.43894);
        msg.parameters[3] = new MessageParameterDouble(3.47392);
        msg.parameters[4] = new MessageParameterDouble(1.32419);

        var encoded = msg.encode();
        var decoded = Message.decode(new DataInputStream(new ByteArrayInputStream(encoded)));

        decoded.parameters[0] = decoded.parameters[0].asString();
        decoded.parameters[1] = decoded.parameters[1].asDouble();
        decoded.parameters[2] = decoded.parameters[2].asDouble();
        decoded.parameters[3] = decoded.parameters[3].asDouble();
        decoded.parameters[4] = decoded.parameters[4].asDouble();

        Assert.assertEquals(msg, decoded);
    }

    @Test
    public void testEquals() {
        var a = new Message(MessageType.PING);
        var b = new Message(MessageType.PING);
        Assert.assertEquals(a, b);

        a.parameters = new MessageParameter[]{new MessageParameterDouble(5)};
        b.parameters = new MessageParameter[]{new MessageParameterDouble(5)};
        Assert.assertEquals(a, b);

        a.parameters = new MessageParameter[]{new MessageParameterDouble(5)};
        b.parameters = new MessageParameter[]{new MessageParameterDouble(10)};
        Assert.assertNotEquals(a, b);

        a = new Message(MessageType.INCREMENT);
        b = new Message(MessageType.PLAYBACK);
        Assert.assertNotEquals(a, b);
    }
}
