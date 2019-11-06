package nl.liacs.watch.protocol.server;

import nl.liacs.watch.protocol.types.Message;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

public class Connection implements nl.liacs.watch.protocol.Connection {
    Socket socket;

    public Connection(Socket socket) {
        this.socket = socket;
    }

    @Override
    public boolean isOpen() {
        return this.socket.isConnected();
    }

    @Override
    public Message receive() throws IOException {
        var stream = new DataInputStream(this.socket.getInputStream());
        return Message.decode(stream);
    }

    @Override
    public void send(Message message) throws Exception {
        this.socket.getOutputStream().write(message.encode());
    }

    @Override
    public void close() throws IOException {
        this.socket.close();
    }
}
