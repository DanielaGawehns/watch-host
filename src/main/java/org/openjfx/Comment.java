package org.openjfx;

import java.time.LocalTime;

/**
 * Class for storing comments about specific measurements of a specific watch
 */
class Comment {

    /**
     * Stores the starting times from the comments
     */
    private LocalTime startingTime;

    /**
     * Stores the end times from the comments
     */
    private LocalTime endTime;

    /**
     * Stores the text from the comments
     */
    private String commentBody;

    /**
     * Stores the type of the comments
     */
    private String commentType;


    /**
     * Getter for {@link Comment#startingTime}
     */
    LocalTime getStartingTime() { return startingTime; }


    /**
     * Getter for {@link Comment#endTime}
     */
    LocalTime getEndTime() { return endTime; }


    /**
     * Getter for {@link Comment#commentBody}
     */
    public String getCommentBody() { return commentBody; }


    /**
     * Setter for {@link Comment#commentType}
     */
    public String getCommentType() { return commentType; }

    /**
     * Setter for {@link Comment#startingTime}
     */
    void setStartingTime(LocalTime st) { this.startingTime = st; }


    /**
     * Setter for {@link Comment#endTime}
     */
    void setEndTime(LocalTime et) { this.endTime = et; }


    /**
     * Setter for {@link Comment#commentBody}
     */
    public void setCommentBody(String cb) { this.commentBody = cb; }


    /**
     * Setter for {@link Comment#commentType}
     */
    public void setCommentType(String ct) { this.commentType = ct; }
}
