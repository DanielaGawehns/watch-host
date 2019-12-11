package nl.liacs.watch.protocol.server;

import nl.liacs.watch.protocol.types.Constants;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * Handles receiving of and replying to host/watch discovery broadcasts.
 */
public class BroadcastHandler {
    private static final byte[] listenBytes = "HelloWorld!\0".getBytes();
    private static final byte[] answerBytes = "WatchSrvrPing\0".getBytes();
    private static final int timeout = 500; // in milliseconds

    /**
     * Listen to broadcasts from watches and reply to them.
     *
     * @throws IOException IO error when listening or sending fails.
     */
    public static void Listen() throws IOException {
        DatagramSocket server = new DatagramSocket();
        server.setSoTimeout(timeout);

        while (!Thread.interrupted()) {
            var bytes = new byte[listenBytes.length];
            var packet = new DatagramPacket(bytes, bytes.length);
            try {
                server.receive(packet);
            } catch (SocketTimeoutException e) {
                continue;
            } catch (Exception e) {
                throw e;
            }

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
            server.send(packet);
        }
    }
}
