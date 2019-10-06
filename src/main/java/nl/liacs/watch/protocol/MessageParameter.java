package nl.liacs.watch.protocol;

public class MessageParameter {
    ParameterType type;
    Object value;

    // REVIEW: there must be a better exception to throw here.

    String getString() {
        if (type != ParameterType.STRING) {
            throw new IllegalArgumentException("type is not a string");
        }
        return (String) value;
    }

    Double getDouble() {
        if (type != ParameterType.DOUBLE) {
            throw new IllegalArgumentException("type is not a double");
        }
        return (Double) value;
    }

    Integer getInteger() {
        if (type != ParameterType.INTEGER) {
            throw new IllegalArgumentException("type is not a int");
        }
        return (Integer) value;
    }

    public String toString() {
        return type.name() + "!" + value.toString();
    }
}
