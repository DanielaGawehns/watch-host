package nl.liacs.watch.protocol.types;

/**
 * Static class containing some useful constants.
 */
public class Constants {
    /**
     * Port constant for the broadcast receiver on the host.
     * Watch should send to here.
     */
    public static final int BroadcastHostPort = 2112;
    /**
     * Port constant for the broadcast receiver on the watch.
     * Host should send to here.
     */
    public static final int BroadcastWatchPort = 2113;
    /**
     * Port constant for the TCP server on the host.
     */
    public static final int TcpPort = 2114;
}
