package org.openjfx;

public class Pair<firstType, secondType> {
    private firstType first;
    private secondType second;

    public Pair(firstType first, secondType second) {
        this.first = first;
        this.second = second;
    }

    public firstType first() { return this.first; }
    public secondType second() { return this.second; }

    public void setFirst(firstType newValue) { this.first = newValue; }
    public void setSecond(secondType newValue) { this.second = newValue; }
}
