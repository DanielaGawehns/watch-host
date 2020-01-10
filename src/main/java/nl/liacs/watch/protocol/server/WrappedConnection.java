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
import java.util.function.Consumer;

/**
 * A wrapped connection, with convenience methods.
 */
public class WrappedConnection implements Closeable {
    private final Connection connection;
    private final BlockingQueue<Message> receiveQueue;
    private final ExecutorService pool;

    /**
     * The highest ID seen on this connection
     */
    private int highestId = 0;
    /**
     * Mapping between message ID and a list of futures waiting on the result
     * for that ID.
     */
    private final HashMap<Integer, List<CompletableFuture<MessageParameter[]>>> replyFutureMap = new HashMap<>();

    /**
     * @param connection The connection to wrap
     */
    WrappedConnection(@NotNull Connection connection) {
        this.pool = Executors.newFixedThreadPool(2);
        this.connection = connection;
        this.receiveQueue = new LinkedBlockingQueue<>();

        Consumer<Callable<Boolean>> doWork = (fn) -> {
            boolean mustClose = false;

            try {
                mustClose = fn.call();
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
            }
        };

        // receive loop
        this.pool.submit(() -> {
            while (!this.pool.isShutdown()) {
                doWork.accept(() -> {
                    var msg = this.connection.receive();
                    this.handleMessage(msg);
                    return false;
                });
            }
        });

        // PING loop
        this.pool.submit(() -> {
            while (!this.pool.isShutdown()) {
                doWork.accept(() -> {
                    try {
                        Thread.sleep(2500);
                    } catch (InterruptedException e) {
                        System.out.println(e);
                    }

                    var msg = this.makeMessageWithID(MessageType.PING);
                    this.send(msg);
                    return false;
                });
            }
        });
    }

    /**
     * @param socket The socket to wrap
     * @throws IOException              IO error when failing to communicate.
     * @throws UnknownProtocolException Unknown protocol error when the client is running an unsupported protocol version.
     */
    WrappedConnection(@NotNull Socket socket) throws IOException, UnknownProtocolException {
        this(new Connection(socket));
    }

    /**
     * Send the given message to the client, without waiting for any possible
     * result.
     *
     * @param message The message to send.
     * @throws IOException IO error when failing to send the message.
     */
    public void send(@NotNull Message message) throws IOException {
        if (connection.isClosed()) {
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
     * @return Whether or not the connection is currently closed.
     */
    public boolean isClosed() {
        return this.connection.isClosed();
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
        if (msg.id > this.highestId) {
            this.highestId = msg.id;
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

            var status = msg.parameters[0].asInteger().getValue();
            var errored = status != 0;

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
        return new Message(++this.highestId, type);
    }

    /**
     * Retrieve the given key from the remote party.
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

    /**
     * Set the value of the given key at the remote party.
     * If the keys and/or value(s) were invalid, the returned future completes
     * with a {@link ReplyException} exception.
     *
     * @param key The key to set
     * @param values The values to set the key to.
     * @return The values as set in the remote key/value store, note that these
     * values can be chaned by the remote party.
     * @throws IOException IO error when failing to send the request.
     */
    public CompletableFuture<MessageParameter[]> setValues(String key, byte[]... values) throws IOException {
        var msg = this.makeMessageWithID(MessageType.SET_VALUES);

        msg.parameters = new MessageParameter[1+values.length];
        msg.parameters[0] = new MessageParameterString(key);
        for (int i = 0; i < values.length; i++) {
            msg.parameters[i+1] = new MessageParameterBinary(values[i]);
        }

        return this.sendAndWaitReply(msg);
    }
}
