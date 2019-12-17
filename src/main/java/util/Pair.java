package util;

/**
 * Utility class making pairs
 */
public class Pair<T1, T2> {

    /**
     * The first item of the pair
     */
    private T1 first;

    /**
     * The first item of the pair
     */
    private T2 second;

    /**
     * Create a new pair with the given values.
     */
    public Pair(T1 first, T2 second) {
        this.first = first;
        this.second = second;
    }

    /**
     * Getter for {@link Pair#first}
     */
    public T1 getFirst() { return this.first; }

    /**
     * Getter for {@link Pair#second}
     */
    public T2 getSecond() { return this.second; }

    /**
     * Setter for {@link Pair#first}
     */
    public void setFirst(T1 newValue) { this.first = newValue; }

    /**
     * Setter for {@link Pair#second}
     */
    public void setSecond(T2 newValue) { this.second = newValue; }
}
