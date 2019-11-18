package nl.liacs.watch.protocol.server;

import nl.liacs.watch.protocol.types.Message;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Connection wraps a socket and implements {@link nl.liacs.watch.protocol.Connection}.
 */
public class Connection implements nl.liacs.watch.protocol.Connection {
    private final Socket socket;

    /**
     * Create a new Connection using the given socket.
     * @param socket The socket to wrap.
     */
    public Connection(Socket socket) {
        this.socket = socket;
    }

    /**
     * @inheritDoc
     */
    @Override
    public boolean isOpen() {
        return this.socket.isConnected();
    }

    /**
     * @inheritDoc
     */
    @Override
    public Message receive() throws IOException {
        var stream = new DataInputStream(this.socket.getInputStream());
        return Message.decode(stream);
    }

    /**
     * @inheritDoc
     */
    @Override
    public void send(Message message) throws Exception {
        this.socket.getOutputStream().write(message.encode());
    }

    /**
     * @inheritDoc
     */
    @Override
    public void close() throws IOException {
        this.socket.close();
    }
}
