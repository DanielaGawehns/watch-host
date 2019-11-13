package nl.liacs.watch.protocol.types;

import java.net.InetAddress;

public class HostAndPort {
    public final InetAddress address;
    public final int port;

    public HostAndPort(InetAddress address, int port) {
        this.address = address;
        this.port = port;
    }
}
