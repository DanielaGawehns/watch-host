package org.openjfx;

import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import org.sqlite.*;


/**
 * Class for managing the connection between the front-end and the database
 */
class DBManager {

    /**
     * Set containing all the dataList ID's
     */
    private Set<Integer> dataIDList = new LinkedHashSet<>();


    /**
     * Connects to the database
     * @return The connection. Returns null on fail
     */
    private Connection connect(){
        Connection connection = null;
        String databaseLoc = "jdbc:sqlite:" + System.getProperty("user.dir") + "/database.sqlite";
        try{
            SQLiteConfig config = new SQLiteConfig(); // config for enabling foreign keys
            config.enforceForeignKeys(true);
            connection = DriverManager.getConnection(databaseLoc, config.toProperties());
            System.out.println("DB: Connected database");
        }catch (SQLException e){
            System.out.println(e.getMessage());
        }
        return connection;
    }


    /**
     * Creates a new random dataList ID and stores it in {@link DBManager#dataIDList}. Checks if ID is already in use
     * @return the dataList ID that was generated. -1 on error
     */
    private int getNewDataID(){
        int random;
        for(int i = 0; i < Integer.MAX_VALUE; i++){
            random = new Random().nextInt(Integer.MAX_VALUE);
            if(!dataIDList.contains(random)){
                dataIDList.add(random);
                return random;
            }
        }
        return -1; //TODO: make this more clear
    }


    /**
     * Runs {@link DBManager#insertSensor(int, String, int, int)} with {@link DBManager#getNewDataID()} as {@code dataID}
     * @param ID Watch ID
     * @param sensor Sensor name
     * @param dataListSize Amount of columns of the {@link DataPoint#dataList}
     */
    private void insertSensor(int ID, String sensor, int dataListSize){
        insertSensor(ID, sensor, dataListSize, getNewDataID());
    }


    /**
     * Inserts a new line into the {@code datalists} table and creates a new {@code} data table for the inserted sensor
     * @param ID Watch ID
     * @param sensor Sensor name
     * @param dataListSize Amount of columns of the {@link DataPoint#dataList}
     * @param dataID The ID of the {@code data} table to be created
     */
    private void insertSensor(int ID, String sensor, int dataListSize, int dataID){
        StringBuilder command = new StringBuilder("INSERT INTO datalists(ID, sensor_name, data_ID) VALUES(?, ?, ?)");

        try{
            Connection con = connect();
            PreparedStatement stmt = con.prepareStatement(command.toString());

            stmt.setInt(1, ID);
            stmt.setString(2, sensor);
            stmt.setInt(3, dataID);
            stmt.executeUpdate();
            System.out.println("DB: added sensor " + sensor + " to watch " + ID);

            command = new StringBuilder("CREATE TABLE data" + dataID + "(datetime TEXT NOT NULL, data_1 double NOT NULL ");
            for(int i = 1; i < dataListSize; i++){
                command.append(", data_").append(i + 1).append(" double NOT NULL");
            }
            command.append(", UNIQUE(datetime));");
            System.out.println("DB: " + command);
            stmt = con.prepareStatement(command.toString());
            stmt.executeUpdate();

            System.out.println("DB: added a datalist with dataID " + dataID);

            stmt.close();
            con.close();
        }catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }


    /**
     * Gets the dataListID from given watch ID and sensor name
     * @param ID Watch ID
     * @param sensor Sensor Name
     * @return DataListID as Integer
     */
    private int getDatalistID(int ID, String sensor){
        System.out.println("DB: getDatalistID with ID "+ ID + " and sensor " + sensor);
        String command = "SELECT * FROM datalists WHERE ID is ? AND sensor_name is ?";
        int dataID = -1;

        try{
            Connection con = connect();
            PreparedStatement stmt = con.prepareStatement(command);
            System.out.println(stmt);
            stmt.setInt(1, ID);
            stmt.setString(2, sensor);

            ResultSet rs = stmt.executeQuery();
            while(rs.next()){
                dataID = rs.getInt("data_ID");
            }

            rs.close();
            stmt.close();
            con.close();
        }catch (SQLException e){
            System.out.println(e.getMessage());
        }
        System.out.println("DB: found dataID " + dataID);
        return dataID;
    }


    /**
     * Drops a table from the database
     * @param name Table name to be dropped
     */
    private void dropTable(String name){
        String command = "DROP TABLE " + name;
        System.out.println(command);

        try{
            Connection con = connect();
            PreparedStatement stmt = con.prepareStatement(command);
            stmt.executeUpdate();

            System.out.println("DB: Dropped table " + name);

            stmt.close();
            con.close();
        }catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }


    /**
     * Gets a list of watch ID's from the {@code smartwatch} table
     * @return List of integers containing the ID's
     */
    List<Integer> getAllWatchId() {
        String command = "SELECT ID FROM smartwatch";
        List<Integer> watchIDList = new ArrayList<>();

        try{
            Connection con = connect();
            PreparedStatement stmt = con.prepareStatement(command);
            ResultSet rs = stmt.executeQuery();

            while(rs.next()){
                watchIDList.add(rs.getInt("ID"));
            }
            stmt.close();
            rs.close();
            con.close();
        }catch (SQLException e){
            System.out.println(e.getMessage());
        }
        return watchIDList;
    }


    /**
     * Gets a list of sensors from the watch with watchID {@code ID}
     * @param ID The ID of the watch
     * @return A list of Strings containing the name of a sensor
     */
    List<String> getSensorList(int ID){
        String command = "SELECT sensor_name FROM datalists WHERE ID = ?";
        List<String> sensorNameList = new ArrayList<>();

        try{
            Connection con = connect();
            PreparedStatement stmt = con.prepareStatement(command);
            stmt.setInt(1, ID);

            ResultSet rs = stmt.executeQuery();

            while(rs.next()){
                sensorNameList.add(rs.getString("sensor_name"));
            }
            stmt.close();
            rs.close();
            con.close();
        }catch (SQLException e){
            System.out.println(e.getMessage());
        }
        return sensorNameList;
    }


    /**
     * Inserts a new line into the {@code smartwatch} table containing the new watch info
     * @param ID Watch ID
     * @param watchName Watch name
     */
    void insertWatch(int ID, String watchName){
        String command = "INSERT INTO smartwatch(ID, name) VALUES(?, ?)";

        try{
            Connection con = connect();
            PreparedStatement stmt = con.prepareStatement(command);

            stmt.setInt(1, ID);
            stmt.setString(2, watchName);
            stmt.executeUpdate();

            System.out.println("DB: added watch with ID " + ID);

            stmt.close();
            con.close();

        }catch (SQLException e){
            System.out.println(e.getMessage());
        }

    }


    /**
     * Gets the name of the watch with ID {@code ID} from the {@code smartwatch} table
     * @param ID Watch ID
     * @return Name of the watch as String
     */
    String getWatchName(int ID){
        String command = "SELECT name FROM smartwatch WHERE ID = ?";
        String name = "";

        try{
            Connection con = connect();
            PreparedStatement stmt = con.prepareStatement(command);
            stmt.setInt(1, ID);
            ResultSet rs = stmt.executeQuery();

            while(rs.next()){
                name = rs.getString("name");
            }
            stmt.close();
            rs.close();
            con.close();
        }catch (SQLException e){
            System.out.println(e.getMessage());
        }
        return name;
    }


    /**
     * Sets the name of the watch with ID {@code ID} in table {@code smartwatch} to given value.
     * @param ID Watch ID
     * @param name Watch name
     */
    void setWatchName(int ID, String name){
        String command = "UPDATE smartwatch SET name = ? WHERE ID = ?";

        try{
            Connection con = connect();
            PreparedStatement stmt = con.prepareStatement(command);

            stmt.setString(1, name);
            stmt.setInt(2, ID);
            stmt.executeUpdate();

            System.out.println("DB: set name of  watch with ID " + ID + " to " + name);

            stmt.close();
            con.close();
        }catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }


    /**
     * Inserts new data into the respecting {@code data} tables
     * @param ID WatchID
     * @param dataList {@link SensorData} holding all the data to be inserted
     * @return {@code 0} on success. {@code -1} on failure
     */
    int insertDatalist(int ID, SensorData dataList) {
        System.out.println("DB: insertDatalist");
        int dataID = getDatalistID(ID, dataList.getSensor()); // Check if dataListID exists
        int size = dataList.getDataFieldsNumber(); // Get size of dataList
        StringBuilder command;
        Connection con = null;

        if(dataID == -1){ // if dataListID does not exist
            dataID = getNewDataID();
            insertSensor(ID, dataList.getSensor(), size, dataID); // insert the new sensor
        }

        try{
            con = connect();
            PreparedStatement stmt = null;
            con.setAutoCommit(false); // disable auto commit to improve performance
            System.out.println("DB: size of added list is " + dataList.size());

            // Go through all DataPoints
            for(int i = 0; i < dataList.size(); i++){
                LocalDate date = dataList.get(i).getDate();
                LocalTime time = dataList.get(i).getTime();
                String datetime = date.toString() + " " + time.toString();
                Double data1 = dataList.get(i).getDataList().get(0);

                // Build command string to deal with any data table format
                command = new StringBuilder("INSERT INTO data" + dataID + "(datetime, data_1");

                for(int j = 1; j < size; j++){
                    command.append(",data_").append(j + 1);
                }
                command.append(") VALUES(?,?");
                command.append(",?".repeat(Math.max(0, size - 1)));
                command.append(")");

                // Setup values and execute command
                System.out.println("DB: " + command + " VALUES: " + datetime + "," + data1);
                stmt = con.prepareStatement(command.toString());
                stmt.setString(1, datetime);
                stmt.setDouble(2, data1);

                for (int j = 1; j < size; j++) {
                    stmt.setDouble(j+2, dataList.get(i).getDataList().get(j));
                }

                try{
                    stmt.executeUpdate();
                }catch (SQLException e){ // Catch Duplicate errors
                    if(e.getErrorCode() != 19){ // If other error detected, pass on
                        throw e;
                    }else{
                        System.out.println("DB: duplicate filtered!");
                    }
                }
            }

            con.commit(); // Commit changes to database
            System.out.println("DB: added watch with ID " + ID);

            if(stmt != null){
                stmt.close();
            }
            con.close();

        }catch (SQLException e){
            System.out.println(e.getMessage());
            try{
                con.rollback(); // rollback to previous state
                con.close();
            }catch (SQLException e1){
                System.out.println(e.getMessage());
            }

            return -1;
        }
        return 0;
    }


    /**
     * Runs {@link DBManager#getDataList(int, String, LocalTime, LocalTime)} with MIN and MAX time values to get all
     * data available
     * @param ID Watch ID
     * @param sensor Sensor Name
     * @return {@link SensorData} containing the data
     */
    SensorData getDataList(int ID, String sensor){
        return getDataList(ID, sensor, LocalTime.MIN,LocalTime.MAX);
    }


    /**
     * Runs {@link DBManager#getAllDataLists(int, LocalTime, LocalTime)} with MIN and MAX time values to get all
     * data available
     * @param ID Watch ID
     * @return List of {@link SensorData} containing the data
     */
    List<SensorData> getAllDataLists(int ID){
        return getAllDataLists(ID, LocalTime.MIN, LocalTime.MAX);
    }


    /**
     * Gets the data of every sensor of a watch of a given time period
     * @param ID Watch ID
     * @param startTime Start time
     * @param endTime End time
     * @return List of {@link SensorData} containing the data
     */
    List<SensorData> getAllDataLists(int ID, LocalTime startTime, LocalTime endTime){
        List<SensorData> list = new ArrayList<>();
        List<String> sensorList = getSensorList(ID);

        for(String sensor : sensorList){
            list.add(getDataList(ID, sensor, startTime, endTime));
        }
        return list;
    }


    /**
     * Gets the data from the {@code data} table between the time values start and end.
     * @param ID Watch ID
     * @param sensor Sensor name
     * @param startTime Start time
     * @param endTime End time
     * @return {@link SensorData} containing the data
     */
    SensorData getDataList(int ID, String sensor, LocalTime startTime, LocalTime endTime){
        int dataID = getDatalistID(ID, sensor);
        int columns;
        String command = "SELECT * FROM data" + dataID + " WHERE datetime BETWEEN ? AND ?";
        int dataListSizeCount = 0;
        SensorData data = null;
        LocalTime time;
        LocalDate date;
        List<Double> dataList;

        System.out.println("DB: " + command);
        try{
            Connection con = connect();

            // Setup SensorData for extraction
            DatabaseMetaData md = con.getMetaData();
            ResultSet rs1 = md.getColumns(null, null, "data" + dataID,
                    null); // Get all columns of the data table
            ResultSetMetaData rmd = rs1.getMetaData();
            for(int i = 1; i <= rmd.getColumnCount(); i++){
                if(rmd.getColumnName(i).contains("data_")){ // If it is a data column
                    dataListSizeCount++;
                }
            }
            data = new SensorData(ID, sensor, dataListSizeCount);

            // Execute command
            PreparedStatement stmt = con.prepareStatement(command);
            stmt.setTime(1, Time.valueOf(startTime));
            stmt.setTime(2, Time.valueOf(endTime));
            ResultSet rs = stmt.executeQuery();
            ResultSetMetaData rsmd = rs.getMetaData();
            columns = rsmd.getColumnCount() - 1;
            System.out.println("DB: columns is " + columns);

            while(rs.next()){
                dataList = new ArrayList<>();
                String[] strings = rs.getString("datetime").split(" ");
                date = LocalDate.parse(strings[0]);
                time = LocalTime.parse(strings[1]);

                for(int i = 0; i < columns; i++){
                    dataList.add(rs.getDouble("data_" + (i+1)));
                }
                DataPoint point = new DataPoint(sensor, date, time, dataList);
                data.add(point);
            }

            stmt.close();
            rs.close();
            con.close();
        }catch (SQLException e){
            System.out.println(e.getMessage());
        }
        System.out.println("DB: found sensordata: ");
        if(data != null){
            data.printRecords();
        }
        return data;
    }


    /**
     * Removes a smartwatch from the database. Will also delete related sensors and data tables
     * @param ID watch ID
     */
    void removeSmartwatch(int ID){
        System.out.println("DB: removeSmartwatch");
        String command = "DELETE FROM smartwatch WHERE ID = ?";
        List<String> sensorList = getSensorList(ID);
        System.out.println(sensorList);

        try{
            for(String sensor : sensorList){
                dropTable("data" + getDatalistID(ID, sensor));
            }

            Connection con = connect();
            PreparedStatement stmt = con.prepareStatement(command);
            stmt.setInt(1, ID);
            stmt.executeUpdate();

            stmt.close();
            con.close();
        }catch (SQLException e){
            System.out.println(e.getMessage());
        }
        System.out.println("DB: Deleted watch " + ID);
    }

}
