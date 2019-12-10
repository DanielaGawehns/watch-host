package org.openjfx;

/**
 * Class holding information about a subject wearing a watch
 * THIS CLASS IS CURRENTLY NOT USED
 */
public class SubjectData { // TODO: do something with this

    /**
     * The ID of the subject
     */
    private int subjectID;

    /**
     * The first name of the subject
     */
    private String firstName;

    /**
     * The last name of the subject
     */
    private String lastName;

    /**
     * The age of the subject
     */
    private int age;


    /**
     * Constructor
     */
    SubjectData(int subjectID) {
        this.subjectID = subjectID;
    }


    /**
     * Getter for {@link SubjectData#subjectID}
     */
    public int getSubjectID() {
        return subjectID;
    }


    /**
     * Setter for {@link SubjectData#subjectID}
     */
    public void setSubjectID(int subjectID) {
        this.subjectID = subjectID;
    }


    /**
     * Getter for {@link SubjectData#firstName}
     */
    public String getFirstName() {
        return firstName;
    }


    /**
     * Setter for {@link SubjectData#firstName}
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }


    /**
     * Getter for {@link SubjectData#lastName}
     */
    public String getLastName() {
        return lastName;
    }


    /**
     * Setter for {@link SubjectData#lastName}
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }


    /**
     * Getter for {@link SubjectData#age}
     */
    public int getAge() {
        return age;
    }


    /**
     * Setter for {@link SubjectData#age}
     */
    public void setAge(int age) {
        this.age = age;
    }
}
