package org.openjfx;

import org.sqlite.SQLiteConfig;
import util.Pair;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;


//TODO maybe add finally blocks to close connections in case of exceptions

/**
 * Class for managing the connection between the front-end and the database
 */
class DBManager {

    /**
     * Set containing all the dataList ID's
     */
    private Set<Integer> dataIDList = new LinkedHashSet<>();

    /**
     * Set containing all the measurement ID's
     */
    private Set<Integer> measurementIDList = new LinkedHashSet<>();

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
     * Generates a new random ID and tries to store it in {@code IDList}
     * @param IDList The set of ID's to generate a new ID for
     * @return Integer containing the added ID or -1 on error
     */
    private int getNewDataID(Set<Integer> IDList){
        int random;
        for(int i = 0; i < Integer.MAX_VALUE; i++){
            random = new Random().nextInt(Integer.MAX_VALUE);
            if(!IDList.contains(random)){
                IDList.add(random);
                return random;
            }
        }
        return -1;
    }


    /**
     * Runs {@link DBManager#insertSensor(int, String, int, int)} with {@link DBManager#getNewDataID()} as {@code dataID}
     * @param ID Watch ID
     * @param sensor Sensor name
     * @param dataListSize Amount of columns of the {@link DataPoint#dataList}
     */
    private void insertSensor(int ID, String sensor, int dataListSize){
        insertSensor(ID, sensor, dataListSize, getNewDataID(dataIDList));
    }


    private void cleanup(Connection con, PreparedStatement stmt){
        try{
            if(stmt != null)
                stmt.close();
        }catch (SQLException e){
            e.printStackTrace();
        }
        try{
            if(con != null)
                con.close();
        }catch (SQLException e){
            e.printStackTrace();
        }
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
        Connection con = null;
        PreparedStatement stmt = null;

        try{
            con = connect();
            stmt = con.prepareStatement(command.toString());

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
        }catch (SQLException e){
            System.out.println(e.getMessage());
        }finally {
            cleanup(con, stmt);
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
        Connection con = null;
        PreparedStatement stmt = null;

        try{
            con = connect();
            stmt = con.prepareStatement(command);
            System.out.println(stmt);
            stmt.setInt(1, ID);
            stmt.setString(2, sensor);

            ResultSet rs = stmt.executeQuery();
            while(rs.next()){
                dataID = rs.getInt("data_ID");
            }

            rs.close();
        }catch (SQLException e){
            System.out.println(e.getMessage());
        }finally {
            cleanup(con, stmt);
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
        Connection con = null;
        PreparedStatement stmt = null;

        System.out.println(command);

        try{
            con = connect();
            stmt = con.prepareStatement(command);
            stmt.executeUpdate();

            System.out.println("DB: Dropped table " + name);
        }catch (SQLException e){
            System.out.println(e.getMessage());
        }finally {
            cleanup(con, stmt);
        }
    }


    /**
     * Gets all watches from the database. Used {@link DBManager#getWatchData(int)}
     * @return {@link SmartwatchList} containing all the {@link Smartwatch} connected
     */
    SmartwatchList getAllWatches(){
        String command = "SELECT * FROM smartwatch";
        Connection con = null;
        PreparedStatement stmt = null;
        WatchData data;
        Smartwatch watch;
        SmartwatchList list = new SmartwatchList();

        try{
            con = connect();
            stmt = con.prepareStatement(command);
            ResultSet rs = stmt.executeQuery();

            while(rs.next()){
                data = getWatchData(rs.getInt(1));
                if(data != null){
                    watch = new Smartwatch(data, rs.getString(2));
                    list.add(watch);
                    System.out.println("Got watch with ID " + rs.getInt(1));
                }
            }

            rs.close();
        }catch (SQLException e){
            System.out.println(e.getMessage());
        }finally {
            cleanup(con, stmt);
        }
        return list;
    }


    /**
     * Gets the WatchData from a watch
     * @param ID Watch ID
     * @return The WatchData
     */
    private WatchData getWatchData(int ID){
        String command = "SELECT * FROM watch_data WHERE ID = ?";
        Connection con = null;
        PreparedStatement stmt = null;
        WatchData data = null;

        try{
            con = connect();
            stmt = con.prepareStatement(command);
            stmt.setInt(1, ID);
            ResultSet rs = stmt.executeQuery();

           if(rs.next()){
               data = new WatchData(ID, rs.getInt(3), rs.getFloat(4), rs.getFloat(5), rs.getString(2));
           }

            rs.close();
        }catch (SQLException e){
            System.out.println(e.getMessage());
        }finally {
            cleanup(con, stmt);
        }
        System.out.println("Got new WatchData for watch with ID " + ID);
        return data;
    }


    /**
     * Gets a list of sensors from the watch with watchID {@code ID}
     * @param ID The ID of the watch
     * @return A list of Strings containing the name of a sensor
     */
    List<String> getSensorList(int ID){
        String command = "SELECT sensor_name FROM datalists WHERE ID = ?";
        List<String> sensorNameList = new ArrayList<>();
        Connection con = null;
        PreparedStatement stmt = null;

        try{
            con = connect();
            stmt = con.prepareStatement(command);
            stmt.setInt(1, ID);

            ResultSet rs = stmt.executeQuery();

            while(rs.next()){
                sensorNameList.add(rs.getString("sensor_name"));
            }

            rs.close();
        }catch (SQLException e){
            System.out.println(e.getMessage());
        }finally {
            cleanup(con, stmt);
        }
        return sensorNameList;
    }


    /**
     * Inserts a new line into the {@code smartwatch} table containing the new watch info
     * @param watch Smartwatch to be added
     */
    void insertWatch(Smartwatch watch){
        String command = "INSERT INTO smartwatch(ID, name) VALUES(?, ?)";
        Connection con = null;
        PreparedStatement stmt = null;

        try{
            con = connect();
            stmt = con.prepareStatement(command);

            stmt.setInt(1, watch.getWatchID());
            stmt.setString(2, watch.getWatchName());
            stmt.executeUpdate();

            System.out.println("DB: added watch with ID " + watch.getWatchID());
        }catch (SQLException e){
            System.out.println(e.getMessage());
        }finally {
            cleanup(con, stmt);
        }
        insertWatchData(watch.getWatchData());
    }


    /**
     * Inserts the watch data into the database
     * @param data WatchData to be added
     */
    private void insertWatchData(WatchData data){
        String command = "INSERT INTO watch_data VALUES(?, ?, ?, ?, ?)";
        Connection con = null;
        PreparedStatement stmt = null;

        try{
            con = connect();
            stmt = con.prepareStatement(command);

            stmt.setInt(1, data.getWatchID());
            stmt.setString(2, data.getIpAdress());
            stmt.setInt(3, data.getBatteryPercentage());
            stmt.setFloat(4, data.getMaxStorage());
            stmt.setFloat(5, data.getUsedStorage());
            stmt.executeUpdate();

            System.out.println("DB: added watchData with ID " + data.getWatchID());
        }catch (SQLException e){
            System.out.println(e.getMessage());
        }finally {
            cleanup(con, stmt);
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
        Connection con = null;
        PreparedStatement stmt = null;

        try{
            con = connect();
            stmt = con.prepareStatement(command);
            stmt.setInt(1, ID);
            ResultSet rs = stmt.executeQuery();

            while(rs.next()){
                name = rs.getString("name");
            }
            rs.close();
        }catch (SQLException e){
            System.out.println(e.getMessage());
        }finally {
            cleanup(con, stmt);
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
        Connection con = null;
        PreparedStatement stmt = null;

        try{
            con = connect();
            stmt = con.prepareStatement(command);

            stmt.setString(1, name);
            stmt.setInt(2, ID);
            stmt.executeUpdate();

            System.out.println("DB: set name of  watch with ID " + ID + " to " + name);
        }catch (SQLException e){
            System.out.println(e.getMessage());
        }finally {
            cleanup(con, stmt);
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
        PreparedStatement stmt = null;

        if(dataID == -1){ // if dataListID does not exist
            dataID = getNewDataID(dataIDList);
            insertSensor(ID, dataList.getSensor(), size, dataID); // insert the new sensor
        }

        try{
            con = connect();
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
                    stmt.close();
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

        }catch (SQLException e){
            System.out.println(e.getMessage());
            try{
                con.rollback(); // rollback to previous state
                con.close();
            }catch (SQLException e1){
                System.out.println(e.getMessage());
            }

            return -1;
        }finally {
            cleanup(con, stmt);
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
        Connection con = null;
        PreparedStatement stmt = null;

        System.out.println("DB: " + command);
        try{
            con = connect();

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
            stmt = con.prepareStatement(command);
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

            rs.close();
        }catch (SQLException e){
            System.out.println(e.getMessage());
        }finally {
            cleanup(con, stmt);
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
        Connection con = null;
        PreparedStatement stmt = null;

        System.out.println(sensorList);

        try{
            for(String sensor : sensorList){
                dropTable("data" + getDatalistID(ID, sensor));
            }

            con = connect();
            stmt = con.prepareStatement(command);
            stmt.setInt(1, ID);
            stmt.executeUpdate();
        }catch (SQLException e){
            System.out.println(e.getMessage());
        }finally {
            cleanup(con, stmt);
        }
        System.out.println("DB: Deleted watch " + ID);
    }

    /**
     * Adds a new measurement to the database
     * @param IDList List of ID's of the watches to add the measurement to
     * @param measurement The measurement to add
     */
    void addMeasurement(List<Integer> IDList, Measurement measurement){
        int measurementID = getNewDataID(measurementIDList);

        createMeasurementTable(measurementID);

        for(var sensor : measurement.getSensors()){
            addToSensorTable(measurementID, sensor.first(), sensor.second());
        }

        for(Integer ID : IDList){
            insertMeasurement(ID, measurement.getDuration(), measurementID);
        }
    }


    /**
     * Adds a new line to a measurement table
     * @param measurementID ID of the table
     * @param sensor Sensor to be added to the measurement
     * @param pollRate Polling rate of said sensor
     */
    private void addToSensorTable(int measurementID, String sensor, Integer pollRate){
        String command = "INSERT INTO measurement" + measurementID + "(sensor, polling_rate) VALUES(?, ?)";
        Connection con = null;
        PreparedStatement stmt = null;

        try{
            con = connect();
            stmt = con.prepareStatement(command);

            stmt.setString(1, sensor);
            stmt.setInt(2, pollRate);
            stmt.executeUpdate();

            System.out.println("DB: added sensor " + sensor + " to measurement table" + measurementID);
        }catch (SQLException e){
            System.out.println(e.getMessage());
        }finally {
            cleanup(con, stmt);
        }
    }


    /**
     * Creates a new measurement table to store sensors and polling rate
     * @param measurementID ID of the table
     */
    private void createMeasurementTable(int measurementID){
        String command = "CREATE TABLE measurement" + measurementID + "(sensor TEXT NOT NULL, polling_rate INTEGER NOT NULL)";
        Connection con = null;
        PreparedStatement stmt = null;

        try{
            con = connect();
            stmt = con.prepareStatement(command);
            stmt.executeUpdate();

            System.out.println("DB: added measurement table with ID " + measurementID);
        }catch (SQLException e){
            System.out.println(e.getMessage());
        }finally {
            cleanup(con, stmt);
        }
    }


    /**
     * Inserts line into measurements table to link measurement to a watch
     * @param ID Watch ID
     * @param duration Duration of measurement
     * @param measurementID ID of measurement table
     */
    private void insertMeasurement(int ID, int duration, int measurementID){
        String command = "INSERT INTO measurements(ID, duration, measurement_ID) VALUES(?, ?, ?) ";
        Connection con = null;
        PreparedStatement stmt = null;

        try{
            con = connect();
            stmt = con.prepareStatement(command);

            stmt.setInt(1, ID);
            stmt.setInt(2, duration);
            stmt.setInt(3, measurementID);
            stmt.executeUpdate();
        }catch (SQLException e){
            System.out.println(e.getMessage());
        }finally {
            cleanup(con, stmt);
        }
    }


    /**
     * Disconnects a measurement from a watch. The measurement Table will NOT be deleted
     * @param ID Watch ID
     */
    void removeMeasurementFromWatch(int ID){
        String command = "DELETE FROM measurements WHERE ID = ?";
        int measurementID = getMeasurementID(ID);
        Connection con = null;
        PreparedStatement stmt = null;

        try{
            con = connect();
            stmt = con.prepareStatement(command);
            stmt.setInt(1, ID);
            stmt.executeUpdate();
        }catch (SQLException e){
            System.out.println(e.getMessage());
        }finally {
            cleanup(con, stmt);
        }
        checkMeasurementClients(measurementID);
    }


    private void checkMeasurementClients(int measurementID){
        String command = "SELECT * FROM measurements WHERE measurement_ID = ?";
        Connection con = null;
        PreparedStatement stmt = null;

        try{
            con  = connect();
            stmt = con.prepareStatement(command);
            stmt.setInt(1, measurementID);
            ResultSet rs = stmt.executeQuery();

            if(!rs.next()){
                dropTable("measurement" + measurementID);
            }
            rs.close();
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }finally {
            cleanup(con, stmt);
        }
    }


    /**
     * Gets the duration of a measurement
     * @param measurementID Measurement ID
     * @return Duration as an Integer. -1 if measurement is not found
     */
    private int getMeasurementDuration(int measurementID){
        String command = "SELECT * FROM measurements WHERE measurement_ID = ?";
        int duration = -1;
        Connection con = null;
        PreparedStatement stmt = null;

        System.out.println(command + " -" + measurementID);
        try{
            con  = connect();
            stmt = con.prepareStatement(command);
            stmt.setInt(1, measurementID);
            ResultSet rs = stmt.executeQuery();

            if(rs.next()){
                duration = rs.getInt("duration");
            }
            rs.close();
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }finally {
            cleanup(con, stmt);
        }
        System.out.println("Found duration " + duration);
        return duration;
    }


    /**
     * Gets the measurement given its ID. Using {@link DBManager#getMeasurementDuration(int)}
     * @param measurementID Measurement ID
     * @return The measurement
     */
    private Measurement getMeasurement(int measurementID){
        String command = "SELECT * FROM measurement" + measurementID;
        Measurement measurement = new Measurement();
        List<Pair<String, Integer>> values = new ArrayList<>();
        Connection con = null;
        PreparedStatement stmt = null;

        try {
            con  = connect();
            stmt = con.prepareStatement(command);
            ResultSet rs = stmt.executeQuery();

            while(rs.next()){
                var pair = new Pair<>(rs.getString(1), rs.getInt(2));
                values.add(pair);
            }
            rs.close();
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }finally {
            cleanup(con, stmt);
        }
        measurement.setSensors(values);
        measurement.setDuration(getMeasurementDuration(measurementID));
        return measurement;
    }


    /**
     * Gets the measurement ID of the measurement on a watch
     * @param ID Watch ID
     * @return ID of the measurement
     */
    private int getMeasurementID(int ID){
        String command = "SELECT * FROM measurements WHERE ID = ?";
        int measurementID = -1;
        Connection con = null;
        PreparedStatement stmt = null;

        try {
            con  = connect();
            stmt = con.prepareStatement(command);
            stmt.setInt(1, ID);
            ResultSet rs = stmt.executeQuery();

            if(rs.next()){
                measurementID = rs.getInt(3);
            }
            rs.close();
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }finally {
            cleanup(con, stmt);
        }
        return measurementID;
    }


    /**
     * Get the measurement of a watch. Using {@link DBManager#getMeasurement(int)}
     * @param ID Watch ID
     * @return The measurement. {@code null} if watch has no active measurement
     */
    Measurement getWatchMeasurement(int ID){
        int measurementID = getMeasurementID(ID);
        if(measurementID > -1){
            return getMeasurement(measurementID);
        }
        return null;
    }

}
