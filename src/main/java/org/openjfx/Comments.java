package org.openjfx;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Class for storing comments about specific measurements of a specific watch
 */
public class Comments {

    /**
     * Stores the starting times from the comments
     */
    private List<Date> startingTimes = new ArrayList<>();

    /**
     * Stores the end times from the comments
     */
    private List<Date> endTimes = new ArrayList<>();

    /**
     * Stores the text from the comments
     */
    private List<String> commentBodies = new ArrayList<>();

    /**
     * Stores the type of the comments
     */
    private List<String> commentTypes = new ArrayList<>();


    /**
     * Check if a lists which contain data about the comment is empty
     */
    Boolean isEmpty() {
        return startingTimes.isEmpty() || endTimes.isEmpty() ||  commentBodies.isEmpty() || commentTypes.isEmpty();
    }


    /**
     * Erase all data about the comments
     */
    void clearAll() {
        startingTimes.clear();
        endTimes.clear();
        commentBodies.clear();
        commentTypes.clear();
    }


    /**
     *
     * @return the size of {@link Comments#startingTimes}
     */
    int size() { return startingTimes.size(); }


    /**
     * Add an element to {@link Comments#startingTimes}
     */
    void addStartingTime(Date s) { startingTimes.add(s); }


    /**
     * Add an element to {@link Comments#endTimes}
     */
    void addEndTime(Date e) { endTimes.add(e); }


    /**
     * Add an element to {@link Comments#commentBodies}
     */
    void addCommentBody(String b) { commentBodies.add(b); }


    /**
     * Add an element to {@link Comments#commentTypes}
     */
    void addCommentType(String t) { commentTypes.add(t); }


    /**
     * Getter for the i'th element in {@link Comments#startingTimes}
     */
    Date getStartingTimeI(int i) { return startingTimes.get(i); }


    /**
     * Getter for the i'th element in {@link Comments#endTimes}
     */
    Date getEndTimeI(int i) { return endTimes.get(i); }


    /**
     * Getter for the i'th element in {@link Comments#commentBodies}
     */
    String getCommentBodyI(int i) { return commentBodies.get(i); }


    /**
     * Getter for the i'th element in {@link Comments#commentTypes}
     */
    String getCommentTypeI(int i) { return commentTypes.get(i); }
}
