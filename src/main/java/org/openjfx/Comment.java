package org.openjfx;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Class for storing comments about specific measurements of a specific watch
 */
public class Comment {

    /**
     * Stores the starting times from the comments
     */
    private Date startingTime;

    /**
     * Stores the end times from the comments
     */
    private Date endTime;

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
    Date getStartingTime() { return startingTime; }


    /**
     * Getter for {@link Comment#endTime}
     */
    Date getEndTime() { return endTime; }


    /**
     * Getter for {@link Comment#commentBody}
     */
    String getCommentBody() { return commentBody; }


    /**
     * Setter for {@link Comment#commentType}
     */
    String getCommentType() { return commentType; }

    /**
     * Setter for {@link Comment#startingTime}
     */
    void setStartingTime(Date st) { this.startingTime = st; }


    /**
     * Setter for {@link Comment#endTime}
     */
    void setEndTime(Date et) { this.endTime = et; }


    /**
     * Setter for {@link Comment#commentBody}
     */
    void setCommentBody(String cb) { this.commentBody = cb; }


    /**
     * Setter for {@link Comment#commentType}
     */
    void setCommentType(String ct) { this.commentType = ct; }
}
