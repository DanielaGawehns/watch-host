package nl.liacs.watch.protocol.server;

import com.google.common.net.HostAndPort;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.*;

public class ConnectionWaiter {
    private final BroadcastReceiver broadcastReceiver;
    private final ExecutorService threadPool;
    private final BlockingQueue<WrappedConnection> wrappedConnections;

    public ConnectionWaiter(int version, int nthreads) throws IOException {
        this.broadcastReceiver = new BroadcastReceiver(version);
        this.threadPool = Executors.newFixedThreadPool(nthreads);
        this.wrappedConnections = new LinkedBlockingQueue<WrappedConnection>();
    }

    public void start() {
        this.threadPool.submit(new Thread(() -> {
            this.broadcastReceiver.Listen();
            while (true) {
                HostAndPort hostAndPort = this.broadcastReceiver.takeHostAndPort();
                this.threadPool.submit(new Thread(() -> {
                    var socket = new Socket(hostAndPort.getHost(), 5050); // TODO: port
                    var connection = new Connection(socket);
                    this.wrappedConnections.add(new WrappedConnection(connection));
                }));
            }
        }));
    }

    public void shutdown() {
        this.threadPool.shutdown();
    }
}
