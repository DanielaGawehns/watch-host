package nl.liacs.watch.protocol.server;

import nl.liacs.watch.protocol.types.Message;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class WrappedConnection {
    private final Connection connection;
    private final BlockingQueue<Message> receiveQueue;
    private final BlockingQueue<Message> sendQueue;

    WrappedConnection(Connection connection) {
        this.connection = connection;
        this.receiveQueue = new LinkedBlockingQueue<>();
        this.sendQueue = new LinkedBlockingQueue<>();
    }

    public void receiveLoop() throws IOException {
        while (true) {
            var msg = this.connection.receive();
            this.receiveQueue.add(msg);
        }
    }

    public void sendLoop() throws Exception {
        while (true) {
            var msg = this.sendQueue.take();
            this.connection.send(msg);
        }
    }
}
