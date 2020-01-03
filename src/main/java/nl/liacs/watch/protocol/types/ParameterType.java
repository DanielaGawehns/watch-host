package nl.liacs.watch.protocol.types;

/**
 * A type of a {@link MessageParameter}
 */
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
