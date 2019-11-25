package nl.liacs.watch.protocol.server;

import nl.liacs.watch.protocol.types.Message;
import nl.liacs.watch.protocol.types.UnknownProtocolException;

import java.io.Closeable;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class WrappedConnection implements Closeable {
    private final Connection connection;
    private final BlockingQueue<Message> receiveQueue;
    private final BlockingQueue<Message> sendQueue;
    private final ExecutorService pool;

    WrappedConnection(Connection connection) {
        this.connection = connection;
        this.receiveQueue = new LinkedBlockingQueue<>();
        this.sendQueue = new LinkedBlockingQueue<>();
        this.pool = Executors.newFixedThreadPool(2);

        // receive loop
        this.pool.submit(new Thread(() -> {
            while (true) {
                try {
                    var msg = this.connection.receive();
                    this.receiveQueue.add(msg);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }));

        // send loop
        this.pool.submit(new Thread(() -> {
            while (true) {
                try {
                    var msg = this.sendQueue.take();
                    this.connection.send(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }));
    }

    WrappedConnection(Socket socket) throws IOException, UnknownProtocolException {
        this(new Connection(socket));
    }

    public void send(Message msg) throws IllegalStateException {
        if (!connection.isOpen()) {
            throw new IllegalStateException("connection is closed");
        }

        this.sendQueue.add(msg);
    }

    public Message receive() throws InterruptedException {
        return this.receiveQueue.take();
    }

    @Override
    public void close() throws IOException {
        this.pool.shutdownNow();
        this.connection.close();
    }
}
