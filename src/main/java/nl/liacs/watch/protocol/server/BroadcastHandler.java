package nl.liacs.watch.protocol.server;

import nl.liacs.watch.protocol.types.Constants;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * Handles receiving of and replying to host/watch discovery broadcasts.
 */
public class BroadcastHandler {
    private static final byte[] listenBytes = "HelloWorld!\0".getBytes();
    private static final byte[] answerBytes = "WatchSrvrPing\0".getBytes();
    private final DatagramSocket server;

    /**
     * Create a new broadcast handler using the default ports.
     *
     * @throws SocketException Socket exception when creating a datagram server fails, for example, if the port is already in use.
     */
    public BroadcastHandler() throws SocketException {
        this.server = new DatagramSocket(Constants.BroadcastHostPort);
    }

    /**
     * Listen to broadcasts from watches and reply to them.
     *
     * @throws IOException IO error when listening or sending fails.
     */
    public void Listen() throws IOException {
        while (true) {
            var bytes = new byte[listenBytes.length];
            var packet = new DatagramPacket(bytes, bytes.length);
            this.server.receive(packet);

            if (!Arrays.equals(bytes, listenBytes)) {
                var rec = new String(listenBytes, StandardCharsets.US_ASCII);
                System.out.format("datagram from %s contained wrong string (%s)\n", packet.getAddress(), rec);
                continue;
            }

            packet = new DatagramPacket(
                    answerBytes,
                    answerBytes.length,
                    packet.getAddress(),
                    Constants.BroadcastWatchPort
            );
            this.server.send(packet);
        }
    }
}
