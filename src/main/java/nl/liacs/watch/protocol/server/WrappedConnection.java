package nl.liacs.watch.protocol.server;

import nl.liacs.watch.protocol.types.*;
import nl.liacs.watch.protocol.types.exceptions.ReplyException;
import nl.liacs.watch.protocol.types.exceptions.UnknownProtocolException;
import org.jetbrains.annotations.NotNull;

import java.io.Closeable;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.*;
import java.util.concurrent.*;

/**
 * A wrapped connection, with convenience methods.
 */
public class WrappedConnection implements Closeable {
    private final Connection connection;
    private final BlockingQueue<Message> receiveQueue;
    private final Thread thread;

    private int latestId = 0;
    private final HashMap<Integer, List<CompletableFuture<MessageParameter[]>>> replyFutureMap = new HashMap<>();

    /**
     * @param connection The connection to wrap
     */
    WrappedConnection(Connection connection) {
        this.connection = connection;
        this.receiveQueue = new LinkedBlockingQueue<>();

        // receive loop
        this.thread = new Thread(() -> {
            while (this.connection.isOpen()) {
                boolean mustClose = false;

                try {
                    var msg = this.connection.receive();
                    this.handleMessage(msg);
                } catch (EOFException | SocketException e) {
                    mustClose = true;
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (mustClose) {
                    try {
                        this.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return;
                }
            }
        });
        this.thread.start();
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
    public void send(@NotNull Message message) throws IOException {
        if (!connection.isOpen()) {
            throw new IllegalStateException("connection is closed");
        }

        this.connection.send(message);
    }

    /**
     * Send the given message and wait for a reply.

     * @param message The message to send.
     * @return A {@link CompletableFuture} that resolves to a list of {@link MessageParameter} of the reply.
     * @throws IOException IO error when failing to send the message.
     * @throws IllegalArgumentException Illegal argument error when the ID of the given message is 0.
     */
    @NotNull
    public CompletableFuture<MessageParameter[]> sendAndWaitReply(@NotNull Message message) throws IOException {
        if (message.id == 0) {
            throw new IllegalArgumentException("message id can't be 0 when expecting reply.");
        }

        this.send(message);

        var future = new CompletableFuture<MessageParameter[]>();
        if (!this.replyFutureMap.containsKey(message.id)) {
            this.replyFutureMap.put(message.id, new ArrayList<>());
        }
        this.replyFutureMap.get(message.id).add(future);
        return future;
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
        this.connection.close();
        this.thread.interrupt();
    }

    /**
     * Handle the newly received {@link Message}.
     * This handles replying to PINGs, completing futures for replies of sent
     * messages, updating {@link latestId}, and putting normal messages in the
     * {@link receiveQueue}.
     *
     * @param msg The message to handle.
     * @throws IOException IO error when failing to send PING reply.
     */
    private void handleMessage(@NotNull Message msg) throws IOException {
        if (msg.id > this.latestId) {
            this.latestId = msg.id;
        }

        if (msg.type.equals(MessageType.PING)) {
            var reply = msg.makeReply(0, "pong");
            this.send(reply);
            return;
        }

        var futures = this.replyFutureMap.getOrDefault(msg.id, Collections.emptyList());
        if (futures.size() == 0) {
            this.receiveQueue.add(msg);
        } else {
            assert msg.type == MessageType.REPLY;

            var status = msg.parameters[0].asDouble().getValue();
            var errored = status > 0;

            if (errored) {
                var message = msg.parameters[1].asString().getValue();
                var exception = new ReplyException(status.intValue(), message);

                for (var future : futures) {
                    future.completeExceptionally(exception);
                }
            } else {
                var parameters = Arrays.copyOfRange(msg.parameters, 2, msg.parameters.length);

                for (var future : futures) {
                    future.complete(parameters);
                }
            }
        }
    }

    /**
     * Make a new {@link Message} with an unique ID and the given {@link MessageType}.
     *
     * @param type The type for the new message.
     * @return A new message with the given type and unique ID.
     */
    @NotNull
    public Message makeMessageWithID(@NotNull MessageType type) {
        var msg = new Message(type);
        msg.id = ++this.latestId;
        return msg;
    }

    /**
     * Create a new {@link Message} that receives the given key from the other
     * side.
     *
     * @param key The key to retrieve the value of.
     * @return A future that resolves to an array of the retrieved values of the key as {@link MessageParameter}.
     * @throws IOException IO error when failing to send the request.
     */
    public CompletableFuture<MessageParameter[]> getValues(String key) throws IOException {
        var msg = this.makeMessageWithID(MessageType.GET_VALUES);
        msg.parameters = new MessageParameter[]{ new MessageParameterString(key) };
        return this.sendAndWaitReply(msg);
    }
}
