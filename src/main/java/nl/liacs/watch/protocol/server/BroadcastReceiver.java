package nl.liacs.watch.protocol.server;

import com.google.common.base.Charsets;
import com.google.common.net.HostAndPort;

import java.io.IOException;
import java.net.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class BroadcastReceiver {
    private final int version;
    private final DatagramSocket listenServer;
    private final DatagramSocket sendServer;
    private final BlockingQueue<HostAndPort> watchQueue;
    private final InetAddress broadcastIP = InetAddress.getByName("255.255.255.255");

    private static final byte[] listenString = "HelloWorld\0".getBytes();
    private static final byte[] answerString = "WatchSrvrPing\0".getBytes();
    private static final int listenPort = 2112;
    private static final int sendPort = 2113;

    public BroadcastReceiver(int version) throws SocketException, UnknownHostException {
        this.listenServer = new DatagramSocket(listenPort);
        this.sendServer = new DatagramSocket(listenPort);
        this.version = version;
        this.watchQueue = new LinkedBlockingQueue<HostAndPort>();
    }

    public void Listen() throws IOException {
        while (true) {
            var bytes = new byte[listenString.length];
            var packet = new DatagramPacket(bytes, bytes.length);
            this.listenServer.receive(packet);

            if (!new String(bytes, Charsets.US_ASCII).equals(listenString)) {
                continue;
            }

            var hostAndPort = HostAndPort.fromParts(
                packet.getAddress().getHostAddress(),
                packet.getPort()
            );

            packet = new DatagramPacket(
                answerString,
                answerString.length,
                this.broadcastIP,
                sendPort
            );
            this.sendServer.send(packet);

            this.watchQueue.add(hostAndPort);
        }
    }

    protected HostAndPort takeHostAndPort() throws InterruptedException {
        return this.watchQueue.take();
    }
}
