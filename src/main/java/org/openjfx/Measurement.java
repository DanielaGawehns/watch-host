package org.openjfx;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Class for storing information about measurements which are to be sent to watches
 */
class Measurement {

    private static final String COMMA_DELIMITER = ",";

    /**
     * Stores the comments from the researcher about the measurement, uses {@link Triplet}
     * The first item is the starting time, second is the end time, third is the actual comment of what occured
     */
    List<Triplet<Date, Date, String>> comments = new ArrayList<>();

    /**
     * List of pairs of sensors which should be measured with a corresponding interval {@link Pair}
     */
    private List<Pair<String, Integer>> sensors = new ArrayList<>();

    /**
     * The duration of the measurement in minutes
     */
    private Integer duration;


    /**
     * Setter for {@link Measurement#sensors}
     */
    public void setSensors(List<Pair<String, Integer>> list) { sensors = list; }


    /**
     * Setter for {@link Measurement#duration}
     */
    public void setDuration(Integer x) { duration = x; }


    /**
     * Getter for {@link Measurement#sensors}
     */
     List<Pair<String, Integer>> getSensors() { return sensors; }

    /**
     * Getter for {@link Measurement#sensors}
     */
    Integer getDuration() { return duration; }

    /**
     * Reads a csv file and parses the data using
     * @param file Specifies file to read fromw
     */
    void readComments(File file){
        //System.out.println("Start reading: " + path);

        try (BufferedReader br = new BufferedReader(new java.io.FileReader(file))) { // read in file
            String line = br.readLine();
            while ((line = br.readLine()) != null) { // go through all the lines

                String[] record = line.split(COMMA_DELIMITER);


                if (record.length < 3) {
                    System.out.println("Record error");
                    throw new ParseException("Length of records too small", 0);
                }

                try {
                    Date t1 = new SimpleDateFormat("kk:mm:ss").parse(record[0]);
                    Date t2 = new SimpleDateFormat("kk:mm:ss").parse(record[1]);
                    String body = record[2];
                    System.out.println("t1: " + t1);
                    System.out.println("t2: " + t2);
                    System.out.println("body: " + body);
                    Triplet<Date, Date, String> comment = new Triplet<>(t1, t2, body);
                    comments.add(comment);
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
       // System.out.println("Done reading: " + path);
    }
}
