package nl.liacs.watch.protocol.tcpserver;

import java.io.IOException;
import java.net.ServerSocket;

public class Server {
    /**
     * @param port The port to bind the server to.
     * @return A newly created ServerSocket.
     * @throws IOException              IO error when creating the server fails.
     * @throws IllegalArgumentException Illegal arugment error when the port is negative
     */
    static public ServerSocket createServer(int port) throws IOException, IllegalArgumentException {
        if (port <= 0) {
            throw new IllegalArgumentException("port must be positive");
        }
        return new ServerSocket(port);
    }
}
