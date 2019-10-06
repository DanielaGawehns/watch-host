package nl.liacs.watch.protocol;

public interface Connection {
    Boolean isOpen();

    String receive();

    void send(String message);

    void close();
}
