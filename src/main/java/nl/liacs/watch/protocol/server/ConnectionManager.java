package nl.liacs.watch.protocol.server;

import nl.liacs.watch.protocol.types.UnknownProtocolException;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class ConnectionManager {
    private final BroadcastHandler broadcastHandler;
    private final ExecutorService threadPool;
    private final BlockingQueue<WrappedConnection> wrappedConnections;
    private final ServerSocket server;

    public ConnectionManager(ServerSocket server) throws IOException {
        this.broadcastHandler = new BroadcastHandler();
        this.server = server;
        this.threadPool = Executors.newCachedThreadPool();
        this.wrappedConnections = new LinkedBlockingQueue<>();
    }

    public void start() {
        // start broadcast handler
        this.threadPool.submit(new Thread(() -> {
            try {
                this.broadcastHandler.Listen();
            } catch (IOException e) {
                e.printStackTrace();
                this.shutdown();
            }
        }));

        this.threadPool.submit(new Thread(() -> {
            while (!this.threadPool.isShutdown()) {
                try {
                    var socket = this.server.accept();
                    this.handleSocket(socket);
                } catch (IOException | UnknownProtocolException e) {
                    e.printStackTrace();
                }
            }
        }));
    }

    public void shutdown() {
        this.threadPool.shutdownNow();
    }

    private void handleSocket(Socket socket) throws IOException, UnknownProtocolException {
        var wrappedConnection = new WrappedConnection(socket);
        this.wrappedConnections.add(wrappedConnection); // REVIEW: add or another function?
    }
}
