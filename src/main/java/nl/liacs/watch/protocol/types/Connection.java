package nl.liacs.watch.protocol.types;

import nl.liacs.watch.protocol.types.Message;

import java.io.IOException;

public interface Connection {
    /**
     * @return Whether or not the connection is open and ready to communicate.
     */
    boolean isOpen();

    /**
     * @return The message received and parsed.
     * @throws IOException IO error on socket failures.
     */
    Message receive() throws IOException;

    /**
     * @param message The message to send.
     * @throws Exception General error when sending failed.
     */
    void send(Message message) throws Exception;

    /**
     * Closes the connection.
     * @throws IOException IO error when closing failed due to socket failure.
     */
    void close() throws IOException;
}