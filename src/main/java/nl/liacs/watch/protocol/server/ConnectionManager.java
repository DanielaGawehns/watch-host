package nl.liacs.watch.protocol.server;

import nl.liacs.watch.protocol.types.UnknownProtocolException;

import java.io.Closeable;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

/**
 * Class that handles all the protocol connections.
 * Starts a {@link BroadcastHandler} to reply to host discovery requests.
 * Wraps an already started TCP server and handles new connections by wrapped it into a {@link WrappedConnection}.
 */
public class ConnectionManager implements Closeable {
    private final List<Consumer<WrappedConnection>> connectionListeners = new ArrayList<>();
    private final ExecutorService threadPool = Executors.newFixedThreadPool(2);
    private final ServerSocket server;
    private boolean running = false;

    /**
     * @param server The TCP server to wrap.
     * @throws SocketException Socket error when failing to create the broadcast handler.
     */
    public ConnectionManager(ServerSocket server) throws SocketException {
        this.server = server;
    }

    /**
     * Start the connection manager.
     */
    public void start() {
        // start broadcast handler
        this.threadPool.submit(() -> {
            try {
                BroadcastHandler.Listen();
            } catch (IOException e) {
                e.printStackTrace();
                try {
                    this.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        this.threadPool.submit(() -> {
            while (!this.threadPool.isShutdown()) {
                try {
                    var socket = this.server.accept();
                    this.handleSocket(socket);
                } catch (IOException | UnknownProtocolException e) {
                    e.printStackTrace();
                }
            }
        });

        this.running = true;
    }

    /**
     * @return Whether or not the connection manager is currently running.
     */
    public boolean isRunning() {
        return this.running;
    }

    public void addConnectionHandler(Consumer<WrappedConnection> consumer) {
        this.connectionListeners.add(consumer);
    }

    /**
     * @param socket The socket to wrap and notify listeners of.
     * @throws IOException              IO error when failing to communicate.
     * @throws UnknownProtocolException Unknown protocol error when the client is running an unsupported protocol version.
     */
    private void handleSocket(Socket socket) throws IOException, UnknownProtocolException {
        var wrappedConnection = new WrappedConnection(socket);
        for (var consumer : this.connectionListeners) {
            consumer.accept(wrappedConnection);
        }
    }

    /**
     * Shut down the connection manager.
     * This stops listening for new sockets and replying to broadcasts.
     * This does _not_ close any created sockets.
     *
     * @throws IOException IO error when closing the server failed.
     */
    @Override
    public void close() throws IOException {
        this.threadPool.shutdownNow();
        this.server.close();
        this.running = false;
    }
}
