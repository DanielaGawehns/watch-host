package nl.liacs.watch.protocol.tests;

import nl.liacs.watch.protocol.server.BroadcastHandler;
import nl.liacs.watch.protocol.types.Constants;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class BroadcastHandlerTests {
    @Test
    public void testBroadcastReceive() throws IOException {
        final byte[] sendBytes = "HelloWorld!\0".getBytes();
        final byte[] listenBytes = "WatchSrvrPing\0".getBytes();

        var watch = new DatagramSocket(Constants.BroadcastWatchPort);
        watch.setBroadcast(true);
        watch.setSoTimeout(2000);

        var handler = new BroadcastHandler();

        var sendPacket = new DatagramPacket(
            sendBytes,
            sendBytes.length,
            InetAddress.getByName("255.255.255.255"),
            Constants.BroadcastHostPort
        );
        watch.send(sendPacket);

        var receiveBytes = new byte[listenBytes.length];
        var receivePacket = new DatagramPacket(receiveBytes, receiveBytes.length);
        watch.receive(receivePacket);

        Assert.assertArrayEquals(listenBytes, receiveBytes);

        watch.close();
        handler.close();
    }
}
