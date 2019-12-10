package nl.liacs.watch.protocol.server;

import nl.liacs.watch.protocol.types.UnknownProtocolException;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Class that handles all the protocol connections.
 * Starts a {@link BroadcastHandler} to reply to host discovery requests.
 * Wraps an already started TCP server and handles new connections by wrapped it into a {@link WrappedConnection}.
 */
public class ConnectionManager {
    private final BroadcastHandler broadcastHandler;
    private final ExecutorService threadPool;
    private final BlockingQueue<WrappedConnection> wrappedConnections;
    private final ServerSocket server;
    private boolean running = false;

    /**
     * @param server The TCP server to wrap.
     * @throws SocketException Socket error when failing to create the broadcast handler.
     */
    public ConnectionManager(ServerSocket server) throws SocketException {
        this.broadcastHandler = new BroadcastHandler();
        this.server = server;
        this.threadPool = Executors.newCachedThreadPool();
        this.wrappedConnections = new LinkedBlockingQueue<>();
    }

    /**
     * Start the connection manager.
     */
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

        this.running = true;
    }

    /**
     * Shut down the connection manager.
     * This stops listening for new sockets and replying to broadcasts.
     * This does _not_ close any created sockets.
     */
    public void shutdown() {
        this.threadPool.shutdownNow();
        this.running = false;
    }

    /**
     * @return Whether or not the connection manager is currently running.
     */
    public boolean isRunning() {
        return this.running;
    }

    /**
     * @param socket The socket to wrap and notify listeners of.
     * @throws IOException              IO error when failing to communicate.
     * @throws UnknownProtocolException Unknown protocol error when the client is running an unsupported protocol version.
     */
    private void handleSocket(Socket socket) throws IOException, UnknownProtocolException {
        var wrappedConnection = new WrappedConnection(socket);
        this.wrappedConnections.add(wrappedConnection); // REVIEW: add or another function?
    }
}
