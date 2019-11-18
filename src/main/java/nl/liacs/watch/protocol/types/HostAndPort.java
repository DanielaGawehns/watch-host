package nl.liacs.watch.protocol.types;

import java.net.InetAddress;

/**
 * Simple class containing address and port combination.
 */
public class HostAndPort {
    final InetAddress address;
    final int port;

    public HostAndPort(InetAddress address, int port) {
        this.address = address;
        this.port = port;
    }
}
