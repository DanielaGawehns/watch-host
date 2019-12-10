package nl.liacs.watch.protocol.server;

import nl.liacs.watch.protocol.types.Message;
import nl.liacs.watch.protocol.types.MessageType;
import nl.liacs.watch.protocol.types.UnknownProtocolException;

import java.io.Closeable;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * A wrapped connection, with convenience methods.
 */
public class WrappedConnection implements Closeable {
    private final Connection connection;
    private final BlockingQueue<Message> receiveQueue;
    private final ExecutorService pool;

    /**
     * @param connection The connection to wrap
     */
    WrappedConnection(Connection connection) {
        this.connection = connection;
        this.receiveQueue = new LinkedBlockingQueue<>();
        this.pool = Executors.newFixedThreadPool(1);

        // receive loop
        this.pool.submit(new Thread(() -> {
            while (!this.pool.isShutdown()) {
                Boolean mustClose = false;

                try {
                    var msg = this.connection.receive();
                    this.handleMessage(msg);
                } catch (EOFException e) {
                    mustClose = true;
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (mustClose) {
                    try {
                        this.connection.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return;
                }
            }
        }));
    }

    /**
     * @param socket The socket to wrap
     * @throws IOException              IO error when failing to communicate.
     * @throws UnknownProtocolException Unknown protocol error when the client is running an unsupported protocol version.
     */
    WrappedConnection(Socket socket) throws IOException, UnknownProtocolException {
        this(new Connection(socket));
    }

    /**
     * Send the given message to the client.
     *
     * @param message The message to send.
     * @throws IOException IO error when failing to send the message.
     */
    public void send(Message message) throws IOException {
        if (!connection.isOpen()) {
            throw new IllegalStateException("connection is closed");
        }

        this.connection.send(message);
    }

    /**
     * Receive the next message from the client.
     * This method blocks.
     *
     * @return The received message.
     * @throws InterruptedException Interrupted exception when the current thread was interrupted.
     */
    public Message receive() throws InterruptedException {
        return this.receiveQueue.take();
    }

    /**
     * @return Whether or not the connection is currently open.
     */
    public Boolean isOpen() {
        return this.connection.isOpen();
    }

    /**
     * Close the connection.
     *
     * @throws IOException IO error when failing to close the connection.
     */
    @Override
    public void close() throws IOException {
        this.pool.shutdownNow();
        this.connection.close();
    }

    private void handleMessage(Message msg) throws IOException {
        if (msg.type.equals(MessageType.PING)) {
            var pong = new Message(MessageType.PONG);
            this.send(pong);
            return;
        }

        this.receiveQueue.add(msg);
    }
}
