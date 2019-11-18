package nl.liacs.watch.protocol.server;

import nl.liacs.watch.protocol.types.Constants;
import nl.liacs.watch.protocol.types.HostAndPort;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Handles receiving of and replying to host/watch discovery broadcasts.
 */
public class BroadcastHandler {
    private static final byte[] listenBytes = "HelloWorld!\0".getBytes();
    private static final byte[] answerBytes = "WatchSrvrPing\0".getBytes();
    private final DatagramSocket listenServer;
    private final DatagramSocket sendServer;
    private final BlockingQueue<HostAndPort> watchQueue;

    /**
     * Create a new broadcast handler using the default ports.
     * @throws SocketException Socket exception when creating a datagram server fails, for example, if the port is already in use.
     */
    public BroadcastHandler() throws SocketException {
        this.listenServer = new DatagramSocket(Constants.BroadcastHostPort);
        this.sendServer = new DatagramSocket(Constants.BroadcastWatchPort);
        this.watchQueue = new LinkedBlockingQueue<>();
    }

    /**
     * Listen to broadcasts from watches, reply to them and put the watch in {@link #watchQueue}.
     * You can take new watches using {@link #takeHostAndPort()}.
     * @throws IOException IO error when listening or sending fails.
     */
    public void Listen() throws IOException {
        while (true) {
            var bytes = new byte[listenBytes.length];
            var packet = new DatagramPacket(bytes, bytes.length);
            this.listenServer.receive(packet);

            var hostAndPort = new HostAndPort(packet.getAddress(), packet.getPort());

            if (!Arrays.equals(bytes, listenBytes)) {
                var rec = new String(listenBytes, StandardCharsets.US_ASCII);
                System.out.format("datagram from %s contained wrong string (%s)\n", hostAndPort, rec);
                continue;
            }

            packet = new DatagramPacket(
                answerBytes,
                answerBytes.length,
                packet.getAddress(),
                Constants.BroadcastWatchPort
            );
            this.sendServer.send(packet);

            this.watchQueue.add(hostAndPort);
        }
    }

    /**
     * @return A new host and port combination, blocks until one is added.
     * @throws InterruptedException Throws when the thread is interrupted.
     */
    protected HostAndPort takeHostAndPort() throws InterruptedException {
        return this.watchQueue.take();
    }
}
