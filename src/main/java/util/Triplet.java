package util;

/**
 * Utility class making pairs
 */
public class Triplet<firstType, secondType, thirdType> {

    /**
     * The first item of the pair
     */
    private firstType first;

    /**
     * The first item of the pair
     */
    private secondType second;

    /**
     * The first item of the pair
     */
    private thirdType third;


    /**
     * Contructor
     */
    public Triplet(firstType first, secondType second, thirdType third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }


    /**
     * Getter for {@link Triplet#first}
     */
    public firstType first() { return this.first; }


    /**
     * Getter for {@link Triplet#second}
     */
    public secondType second() { return this.second; }


    /**
     * Getter for {@link Triplet#second}
     */
    public thirdType third() { return this.third; }


    /**
     * Setter for {@link Triplet#first}
     */
    public void setFirst(firstType newValue) { this.first = newValue; }


    /**
     * Setter for {@link Triplet#second}
     */
    public void setSecond(secondType newValue) { this.second = newValue; }


    /**
     * Setter for {@link Triplet#third}
     */
    public void setThird(thirdType newValue) { this.third = newValue; }
}
