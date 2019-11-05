package nl.liacs.watch.protocol.server;

import java.io.IOException;
import java.net.ServerSocket;

public class Server {
    ServerSocket server;

    Server(int port) throws IOException {
        this.server = new ServerSocket(port);
    }

    public Connection accept() throws IOException {
        return new Connection(this.server.accept());
    }
}
