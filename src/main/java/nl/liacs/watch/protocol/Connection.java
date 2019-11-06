package nl.liacs.watch.protocol;

import nl.liacs.watch.protocol.types.Message;

import java.io.IOException;

public interface Connection {
    boolean isOpen();

    Message receive() throws IOException;

    void send(Message message) throws Exception;

    void close() throws IOException;
}
