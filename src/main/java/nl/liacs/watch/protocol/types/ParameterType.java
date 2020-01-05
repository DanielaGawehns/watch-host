package nl.liacs.watch.protocol.types;

/**
 * A type of a {@link MessageParameter}
 */
public enum ParameterType {
    /**
     * Parameter type is unknown.
     */
    UNKNOWN,
    /**
     * 32-bit signed integer.
     */
    INTEGER,
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
