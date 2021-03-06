package nl.liacs.watch.protocol.server;

import java.io.Closeable;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import org.jetbrains.annotations.NotNull;

import nl.liacs.watch.protocol.types.Message;
import nl.liacs.watch.protocol.types.exceptions.UnknownProtocolException;

/**
 * Connection wraps a socket and implements {@link nl.liacs.watch.protocol.types.Connection}.
 */
public class Connection implements nl.liacs.watch.protocol.types.Connection, Closeable {
    private final Socket socket;
    private final DataInputStream is;
    private final DataOutputStream os;

    /**
     * Create a new Connection using the given socket and does a version handshake.
     *
     * @param socket The socket to wrap.
     * @throws IOException              IO error when failing to read from or send to the socket.
     * @throws UnknownProtocolException When the protocol version is unknown to the server.
     */
    public Connection(@NotNull Socket socket) throws IOException, UnknownProtocolException {
        this.socket = socket;
        this.is = new DataInputStream(socket.getInputStream());
        this.os = new DataOutputStream(socket.getOutputStream());

        // handshake
        var version = this.is.readUnsignedByte();
        if (version != 1) { // simple version check
            this.close();
            throw new UnknownProtocolException("watch runs unknown protocol version", version);
        }

        this.os.writeByte(0x06); // send ACK.
    }

    @Override
    public boolean isClosed() {
        return this.socket.isClosed();
    }

    @Override
    public Message receive() throws IOException {
        return Message.decode(this.is);
    }

    @Override
    public void send(@NotNull Message message) throws IOException {
        this.os.write(message.encode());
    }

    @Override
    public void close() throws IOException {
        this.is.close();
        this.os.close();
        this.socket.close();
    }
}
