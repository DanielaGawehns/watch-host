package nl.liacs.watch.protocol.server;

import nl.liacs.watch.protocol.types.Constants;
import org.jetbrains.annotations.Nullable;

import java.io.Closeable;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * Handles receiving of and replying to host/watch discovery broadcasts.
 */
public class BroadcastHandler implements Closeable {
    private static final byte[] listenBytes = "HelloWorld!\0".getBytes();
    private static final byte[] answerBytes = "WatchSrvrPing\0".getBytes();

    private final DatagramSocket server;
    private final Thread thread;
    private boolean running = true;

    public BroadcastHandler(int rate) throws IOException {
        server = new DatagramSocket();
        server.setSoTimeout(rate);
        this.thread = new Thread(() -> {
            try {
                this.receiveLoop();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        this.thread.start();
    }

    public BroadcastHandler() throws IOException {
        this(500);
    }

    @Override
    public void close() {
        this.thread.interrupt();
        this.server.close();
        this.running = false;
    }

    @Nullable
    private DatagramPacket receivePacket() throws IOException {
        var bytes = new byte[listenBytes.length];
        var packet = new DatagramPacket(bytes, bytes.length);
        try {
            server.receive(packet);
            return packet;
        } catch (SocketTimeoutException e) {
            return null;
        }
    }

    /**
     * Listen to broadcasts from watches and reply to them.
     *
     * @throws IOException IO error when listening or sending fails.
     */
    private void receiveLoop() throws IOException {
        while (this.running) {
            var packet = this.receivePacket();
            if (packet == null) {
                continue;
            }
            var bytes = packet.getData();

            if (!Arrays.equals(bytes, listenBytes)) {
                var rec = new String(listenBytes, StandardCharsets.US_ASCII);
                System.out.format("datagram from %s contained wrong string (%s)\n", packet.getAddress(), rec);
                continue;
            }

            var reply = new DatagramPacket(
                    answerBytes,
                    answerBytes.length,
                    packet.getAddress(),
                    Constants.BroadcastWatchPort
            );
            server.send(reply);
        }
    }
}
