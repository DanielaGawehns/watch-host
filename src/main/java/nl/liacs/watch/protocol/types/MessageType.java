package nl.liacs.watch.protocol.types;

public enum MessageType {
    /**
     * a PING to check if the connection is still open uninterrupted and to
     * check if the other side still responds by sending a PONG.
     */
    PING(0),

    /**
     * a reply to a message send by the other party.
     */
    REPLY(1),

    /**
     * a key-value store retrieval operation.
     */
    GET_VALUES(2),

    /**
     * a key-value store set operation.
     */
    SET_VALUES(3),

    /**
     * an incremental data update for the dashboard, used in live views.
     */
    INCREMENT(4),

    /**
     * older collected data to be send to the dashboard.
     */
    PLAYBACK(5);

    private final int id;

    MessageType(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }
}
