package nl.liacs.watch.protocol.server;

import nl.liacs.watch.protocol.types.Message;

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
    public Message receive() {
        return null;
    }

    @Override
    public void send(Message message) {

    }

    @Override
    public void close() {

    }
}
