package nl.liacs.watch.protocol.server;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class ConnectionWaiter {
    private final BroadcastReceiver broadcastReceiver;
    private final ExecutorService threadPool;
    private final BlockingQueue<WrappedConnection> wrappedConnections;

    public ConnectionWaiter(int version, int nthreads) throws IOException {
        this.broadcastReceiver = new BroadcastReceiver();
        this.threadPool = Executors.newFixedThreadPool(nthreads);
        this.wrappedConnections = new LinkedBlockingQueue<>();
    }

    public void start() {
        /*
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
         */
    }

    public void shutdown() {
        this.threadPool.shutdown();
    }
}
