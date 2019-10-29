package org.openjfx;

// Utility class for making pairs of two items which are related
public class Pair<firstType, secondType> {
    private firstType first;
    private secondType second;

    // Constructor
    public Pair(firstType first, secondType second) {
        this.first = first;
        this.second = second;
    }

    public firstType first() { return this.first; } // get first item
    public secondType second() { return this.second; } // get second item

    public void setFirst(firstType newValue) { this.first = newValue; } // set value of first item
    public void setSecond(secondType newValue) { this.second = newValue; } // set value of second item
}
