package nl.liacs.watch.protocol.types;

public enum ParameterType {
    UNKNOWN,
    /**
     * Double precision IEEE float.
     */
    DOUBLE,
    /**
     * Non null terminated string.
     */
    STRING,
    /**
     * Binary byte sequence
     */
    BINARY,
}
