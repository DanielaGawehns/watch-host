package nl.liacs.watch.protocol;

import nl.liacs.watch.protocol.types.Message;

public interface Connection {
    boolean isOpen();

    Message receive();

    void send(Message message);

    void close();
}
