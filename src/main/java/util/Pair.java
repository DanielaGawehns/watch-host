package util;

/**
 * Utility class making pairs
 */
public class Pair<firstType, secondType> {

    /**
     * The first item of the pair
     */
    private firstType first;

    /**
     * The first item of the pair
     */
    private secondType second;


    /**
     * Contructor
     */
    public Pair(firstType first, secondType second) {
        this.first = first;
        this.second = second;
    }


    /**
     * Getter for {@link Pair#first}
     */
    public firstType first() { return this.first; }


    /**
     * Getter for {@link Pair#second}
     */
    public secondType second() { return this.second; } // get second item


    /**
     * Setter for {@link Pair#first}
     */
    public void setFirst(firstType newValue) { this.first = newValue; } // set value of first item


    /**
     * Setter for {@link Pair#second}
     */
    public void setSecond(secondType newValue) { this.second = newValue; } // set value of second item
}
