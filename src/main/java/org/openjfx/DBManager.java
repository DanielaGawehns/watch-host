package org.openjfx;

import util.Pair;

import java.io.Closeable;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;


/**
 * Class for managing the connection between the front-end and the database
 * For the database sqlite and jdbc are used
 */
public class DBManager implements Closeable {
    private final Connection connection;
    /**
     * Set containing all the dataList ID's
     */
    private Set<Integer> dataIDList = new LinkedHashSet<>();
    /**
     * Set containing all the measurement ID's
     */
    private Set<Integer> measurementIDList = new LinkedHashSet<>();

    public DBManager() throws SQLException, IOException {
        String databaseLoc
            = "jdbc:sqlite:"
            + System.getProperty("user.dir")
            + "/database.sqlite";

        connection = DriverManager.getConnection(databaseLoc);
        connection.createStatement().execute("PRAGMA foreign_keys = ON"); // Make sure foreign keys are enforced
        System.out.println("DB: Connected database");

        this.connection.setAutoCommit(true);

        this.createTables();
    }

    private void createTables() throws IOException, SQLException {
        var stream = this.getClass().getResourceAsStream("/tables.sql");
        var bytes = stream.readAllBytes();
        var statements = new String(bytes).split(";");

        for (var text : statements) {
            text = text.trim();
            if (text.isEmpty()) {
                continue;
            }

            var stmt = this.connection.prepareStatement(text);
            stmt.execute();
        }
    }

    /**
     * Generates a new random ID and tries to store it in {@code IDList}
     *
     * @param IDList The set of ID's to generate a new ID for
     * @return Integer containing the added ID or -1 on error
     */
    private int getNewDataID(Set<Integer> IDList) {
        int random;
        for (int i = 0; i < Integer.MAX_VALUE; i++) {
            random = new Random().nextInt(Integer.MAX_VALUE);
            if (!IDList.contains(random)) {
                IDList.add(random);
                System.out.println("Got new Data ID: " + random);
                return random;
            }
        }
        return -1;
    }


    /**
     * Runs {@link DBManager#insertSensor(int, String, int, int)} with {@link DBManager#getNewDataID(Set)} as {@code dataID}
     *
     * @param ID           Watch ID
     * @param sensor       Sensor name
     * @param dataListSize Amount of columns of the dataLists of the {@link DataPoint}
     */
    private void insertSensor(int ID, String sensor, int dataListSize) {
        insertSensor(ID, sensor, dataListSize, getNewDataID(dataIDList));
    }


    /**
     * Inserts a new line into the {@code datalists} table and creates a new {@code} data table for the inserted sensor
     *
     * @param ID           Watch ID
     * @param sensor       Sensor name
     * @param dataListSize Amount of columns of the dataLists of the {@link DataPoint}
     * @param dataID       The ID of the {@code data} table to be created
     */
    private void insertSensor(int ID, String sensor, int dataListSize, int dataID) {
        StringBuilder command = new StringBuilder("INSERT INTO datalists(ID, sensor_name, data_ID) VALUES(?, ?, ?)");

        System.out.println("Inserting new sensor " + sensor + " with ID " + dataID);

        try (var stmt = this.connection.prepareStatement(command.toString())) {
            stmt.setInt(1, ID);
            stmt.setString(2, sensor);
            stmt.setInt(3, dataID);
            stmt.executeUpdate();
            System.out.println("DB: added sensor " + sensor + " to watch " + ID);

            command = new StringBuilder("CREATE TABLE data" + dataID + "(datetime TEXT NOT NULL, data_1 double NOT NULL ");
            for (int i = 1; i < dataListSize; i++) {
                command.append(", data_").append(i + 1).append(" double NOT NULL");
            }
            command.append(", UNIQUE(datetime));");
            System.out.println("DB: " + command);

            var stmt2 = this.connection.prepareStatement(command.toString());
            stmt2.executeUpdate();
            stmt2.close();

            System.out.println("DB: added a datalist with dataID " + dataID);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }


    /**
     * Gets the dataListID from given watch ID and sensor name
     *
     * @param ID     Watch ID
     * @param sensor Sensor Name
     * @return DataListID as Integer
     */
    private int getDatalistID(int ID, String sensor) {
        System.out.println("DB: getDatalistID with ID " + ID + " and sensor " + sensor);
        String command = "SELECT * FROM datalists WHERE ID is ? AND sensor_name is ?";
        int dataID = -1;

        try (var stmt = this.connection.prepareStatement(command)) {
            System.out.println(stmt);
            stmt.setInt(1, ID);
            stmt.setString(2, sensor);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                dataID = rs.getInt("data_ID");
            }

            rs.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        System.out.println("DB: found dataID " + dataID);
        return dataID;
    }


    /**
     * Drops a table from the database
     *
     * @param name Table name to be dropped
     */
    private void dropTable(String name) {
        String command = "DROP TABLE " + name;

        System.out.println(command);

        try (var stmt = this.connection.prepareStatement(command)) {
            stmt.executeUpdate();

            System.out.println("DB: Dropped table " + name);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }


    /**
     * Gets all watches from the database. Used {@link DBManager#getWatch(int)}
     *
     * @return {@link SmartwatchList} containing all the {@link Smartwatch} connected
     */
    public SmartwatchList getAllWatches() {
        String command = "SELECT * FROM smartwatch";
        Smartwatch watch;
        SmartwatchList list = new SmartwatchList();
        int ID;

        try (var stmt = this.connection.prepareStatement(command)) {
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                ID = rs.getInt(1);
                watch = getWatch(ID);
                if (watch != null) {
                    list.add(watch);
                } else {
                    System.err.println("Smartwatch with ID: " + ID + " not found");
                }
            }
            rs.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return list;
    }


    /**
     * Gets a watch from the database. This includes all the data regarding the watch.
     * Uses {@link DBManager#getWatchData(int)} and {@link DBManager#getSensorList(int)}
     *
     * @param ID Watch ID
     * @return The watch {@link Smartwatch}
     */
    Smartwatch getWatch(int ID) {
        String command = "SELECT * FROM smartwatch WHERE ID = ?";
        Smartwatch watch = null;
        WatchData data;

        try (var stmt = this.connection.prepareStatement(command)) {
            stmt.setInt(1, ID);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                data = getWatchData(ID);
                var sensors = getSensorList(ID);
                if (data != null) {
                    watch = new Smartwatch(data, rs.getString(2), null);
                    for (String sensor : sensors) {
                        watch.addSensor(sensor);
                        watch.setData(getDataList(ID, sensor));

                    }
                    watch.setMeasurement(getWatchMeasurement(ID));
                    watch.setComments(getComments(ID));
                    System.out.println("Got watch with ID " + ID);
                } else {
                    System.err.println("No watchData found for watch with ID: " + ID);
                }
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return watch;
    }


    /**
     * Gets the WatchData from a watch
     *
     * @param ID Watch ID
     * @return The WatchData
     */
    private WatchData getWatchData(int ID) {
        String command = "SELECT * FROM watch_data WHERE ID = ?";
        WatchData data = null;

        try (var stmt = this.connection.prepareStatement(command)) {
            stmt.setInt(1, ID);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                data = new WatchData(ID, rs.getInt(3), rs.getFloat(4), rs.getFloat(5));
            }

            rs.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        System.out.println("Got new WatchData for watch with ID " + ID);
        return data;
    }


    /**
     * Gets a list of sensors from the watch with watchID {@code ID}
     *
     * @param ID The ID of the watch
     * @return A list of Strings containing the name of a sensor
     */
    List<String> getSensorList(int ID) {
        String command = "SELECT sensor_name FROM datalists WHERE ID = ?";
        List<String> sensorNameList = new ArrayList<>();

        try (var stmt = this.connection.prepareStatement(command)) {
            stmt.setInt(1, ID);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                sensorNameList.add(rs.getString("sensor_name"));
            }

            rs.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return sensorNameList;
    }


    /**
     * Inserts a new line into the {@code smartwatch} table containing the new watch info
     *
     * @param watch Smartwatch to be added
     */
    public void insertWatch(Smartwatch watch) {
        String command = "INSERT INTO smartwatch(ID, name) VALUES(?, ?)";

        try (var stmt = this.connection.prepareStatement(command)) {

            stmt.setInt(1, watch.getWatchID());
            stmt.setString(2, watch.getWatchName());
            stmt.executeUpdate();

            System.out.println("DB: added watch with ID " + watch.getWatchID());
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        insertWatchData(watch.getWatchData());
    }


    /**
     * Inserts the watch data into the database
     *
     * @param data WatchData to be added
     */
    private void insertWatchData(WatchData data) {
        String command = "INSERT INTO watch_data VALUES(?, ?, ?, ?, ?)";

        try (var stmt = this.connection.prepareStatement(command)) {

            stmt.setInt(1, data.getWatchID());
            stmt.setString(2, "");
            stmt.setInt(3, data.getBatteryPercentage());
            stmt.setFloat(4, data.getMaxStorage());
            stmt.setFloat(5, data.getUsedStorage());
            stmt.executeUpdate();

            System.out.println("DB: added watchData with ID " + data.getWatchID());
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }


    /**
     * Gets the name of the watch with ID {@code ID} from the {@code smartwatch} table
     *
     * @param ID Watch ID
     * @return Name of the watch as String
     */
    String getWatchName(int ID) {
        String command = "SELECT name FROM smartwatch WHERE ID = ?";
        String name = "";

        try (var stmt = this.connection.prepareStatement(command)) {
            stmt.setInt(1, ID);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                name = rs.getString("name");
            }
            rs.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return name;
    }


    /**
     * Sets the name of the watch with ID {@code ID} in table {@code smartwatch} to given value.
     *
     * @param ID   Watch ID
     * @param name Watch name
     */
    public void setWatchName(int ID, String name) {
        String command = "UPDATE smartwatch SET name = ? WHERE ID = ?";

        try (var stmt = this.connection.prepareStatement(command)) {

            stmt.setString(1, name);
            stmt.setInt(2, ID);
            stmt.executeUpdate();

            System.out.println("DB: set name of  watch with ID " + ID + " to " + name);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }


    /**
     * Inserts new data into the respecting {@code data} tables
     *
     * @param ID       WatchID
     * @param dataList {@link SensorData} holding all the data to be inserted
     * @return {@code 0} on success. {@code -1} on failure
     */
    public int insertDatalist(int ID, SensorData dataList) {
        System.out.println("DB: insertDatalist");
        int dataID = getDatalistID(ID, dataList.getSensor()); // Check if dataListID exists
        int size = dataList.getDataFieldsNumber(); // Get size of dataList
        StringBuilder command;

        if (dataID == -1) { // if dataListID does not exist
            dataID = getNewDataID(dataIDList);
            insertSensor(ID, dataList.getSensor(), size, dataID); // insert the new sensor
        }

        try {
            this.connection.setAutoCommit(false); // disable auto commit to improve performance
            System.out.println("DB: size of added list is " + dataList.size());

            // Go through all DataPoints
            for (int i = 0; i < dataList.size(); i++) {
                LocalDate date = dataList.get(i).getDate();
                LocalTime time = dataList.get(i).getTime();
                String datetime = date.toString() + " " + time.toString();
                Double data1 = dataList.get(i).getDataList().get(0);

                // Build command string to deal with any data table format
                command = new StringBuilder("INSERT INTO data" + dataID + "(datetime, data_1");

                for (int j = 1; j < size; j++) {
                    command.append(",data_").append(j + 1);
                }
                command.append(") VALUES(?,?");
                command.append(",?".repeat(Math.max(0, size - 1)));
                command.append(")");

                // Setup values and execute command
                System.out.println("DB: " + command + " VALUES: " + datetime + "," + data1);
                var stmt = this.connection.prepareStatement(command.toString());
                stmt.setString(1, datetime);
                stmt.setDouble(2, data1);

                for (int j = 1; j < size; j++) {
                    stmt.setDouble(j + 2, dataList.get(i).getDataList().get(j));
                }

                try {
                    stmt.executeUpdate();
                    stmt.close();
                } catch (SQLException e) { // Catch Duplicate errors
                    if (e.getErrorCode() != 19) { // If other error detected, pass on
                        throw e;
                    } else {
                        System.out.println("DB: duplicate filtered!");
                    }
                }
            }

            this.connection.commit(); // Commit changes to database
            System.out.println("DB: added watch with ID " + ID);

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            try {
                this.connection.rollback(); // rollback to previous state
                this.connection.close();
            } catch (SQLException e1) {
                System.out.println(e.getMessage());
            }

            return -1;
        } finally {
            try {
                this.connection.setAutoCommit(true);
                this.connection.commit();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return 0;
    }


    /**
     * Runs {@link DBManager#getDataList(int, String, LocalTime, LocalTime)} with MIN and MAX time values to get all
     * data available
     *
     * @param ID     Watch ID
     * @param sensor Sensor Name
     * @return {@link SensorData} containing the data
     */
    SensorData getDataList(int ID, String sensor) {
        return getDataList(ID, sensor, LocalTime.MIN, LocalTime.MAX);
    }


    /**
     * Runs {@link DBManager#getAllDataLists(int, LocalTime, LocalTime)} with MIN and MAX time values to get all
     * data available
     *
     * @param ID Watch ID
     * @return List of {@link SensorData} containing the data
     */
    List<SensorData> getAllDataLists(int ID) {
        return getAllDataLists(ID, LocalTime.MIN, LocalTime.MAX);
    }


    /**
     * Gets the data of every sensor of a watch of a given time period
     *
     * @param ID        Watch ID
     * @param startTime Start time
     * @param endTime   End time
     * @return List of {@link SensorData} containing the data
     */
    List<SensorData> getAllDataLists(int ID, LocalTime startTime, LocalTime endTime) {
        List<SensorData> list = new ArrayList<>();
        List<String> sensorList = getSensorList(ID);

        for (String sensor : sensorList) {
            list.add(getDataList(ID, sensor, startTime, endTime));
        }
        return list;
    }


    /**
     * Gets the data from the {@code data} table between the time values start and end.
     *
     * @param ID        Watch ID
     * @param sensor    Sensor name
     * @param startTime Start time
     * @param endTime   End time
     * @return {@link SensorData} containing the data
     */
    SensorData getDataList(int ID, String sensor, LocalTime startTime, LocalTime endTime) {
        int dataID = getDatalistID(ID, sensor);
        int columns;
        String command = "SELECT * FROM data" + dataID + " WHERE datetime BETWEEN ? AND ?";
        int dataListSizeCount = 0;
        SensorData data = null;
        LocalTime time;
        LocalDate date;
        List<Double> dataList;

        System.out.println("DB: " + command);
        try {

            // Setup SensorData for extraction
            DatabaseMetaData md = this.connection.getMetaData();
            ResultSet rs1 = md.getColumns(null, null, "data" + dataID,
                    null); // Get all columns of the data table
            ResultSetMetaData rmd = rs1.getMetaData();
            for (int i = 1; i <= rmd.getColumnCount(); i++) {
                if (rmd.getColumnName(i).contains("data_")) { // If it is a data column
                    dataListSizeCount++;
                }
            }
            data = new SensorData(ID, sensor, dataListSizeCount);

            // Execute command
            var stmt = this.connection.prepareStatement(command);
            stmt.setTime(1, Time.valueOf(startTime));
            stmt.setTime(2, Time.valueOf(endTime));
            ResultSet rs = stmt.executeQuery();
            ResultSetMetaData rsmd = rs.getMetaData();
            columns = rsmd.getColumnCount() - 1;
            System.out.println("DB: columns is " + columns);

            while (rs.next()) {
                dataList = new ArrayList<>();
                String[] strings = rs.getString("datetime").split(" ");
                date = LocalDate.parse(strings[0]);
                time = LocalTime.parse(strings[1]);

                for (int i = 0; i < columns; i++) {
                    dataList.add(rs.getDouble("data_" + (i + 1)));
                }
                DataPoint point = new DataPoint(sensor, date, time, dataList);
                data.add(point);
            }

            rs.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        System.out.println("DB: found sensordata: ");
        if (data != null) {
            data.printRecords();
        }
        return data;
    }


    /**
     * Removes a smartwatch from the database. Will also delete related sensors and data tables
     *
     * @param ID watch ID
     */
    public void removeSmartwatch(int ID) {
        System.out.println("DB: removeSmartwatch");
        String command = "DELETE FROM smartwatch WHERE ID = ?";
        List<String> sensorList = getSensorList(ID);
        int measurementID = getMeasurementID(ID);

        System.out.println(sensorList);

        for (String sensor : sensorList) {
            dropTable("data" + getDatalistID(ID, sensor));
        }

        try (var stmt = this.connection.prepareStatement(command)) {
            stmt.setInt(1, ID);
            stmt.executeUpdate();

            checkMeasurementClients(measurementID); // Check if measurement is dangling
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        System.out.println("DB: Deleted watch " + ID);
    }


    /**
     * Adds a new measurement to the database
     *
     * @param IDList      List of ID's of the watches to add the measurement to
     * @param measurement The measurement to add
     */
    public void addMeasurement(List<Integer> IDList, Measurement measurement) {
        int measurementID = getNewDataID(measurementIDList);

        createMeasurementTable(measurementID);

        for (var sensor : measurement.getSensors()) {
            addToSensorTable(measurementID, sensor.first(), sensor.second());
        }

        for (Integer ID : IDList) {
            insertMeasurement(ID, measurement, measurementID);
        }
    }


    /**
     * Adds a new line to a measurement table
     *
     * @param measurementID ID of the table
     * @param sensor        Sensor to be added to the measurement
     * @param pollRate      Polling rate of said sensor
     */
    private void addToSensorTable(int measurementID, String sensor, Integer pollRate) {
        String command = "INSERT INTO measurement" + measurementID + " VALUES(?, ?)";

        try (var stmt = this.connection.prepareStatement(command)) {

            stmt.setString(1, sensor);
            stmt.setInt(2, pollRate);
            stmt.executeUpdate();

            System.out.println("DB: added sensor " + sensor + " to measurement table" + measurementID);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }


    /**
     * Creates a new measurement table to store sensors and polling rate
     *
     * @param measurementID ID of the table
     */
    private void createMeasurementTable(int measurementID) {
        String command = "CREATE TABLE measurement" + measurementID + "(sensor TEXT NOT NULL, polling_rate INTEGER NOT NULL)";

        try (var stmt = this.connection.prepareStatement(command)) {
            stmt.executeUpdate();

            System.out.println("DB: added measurement table with ID " + measurementID);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }


    /**
     * Inserts line into measurements table to link measurement to a watch
     *
     * @param ID            Watch ID
     * @param measurement   The measurement to be added
     * @param measurementID ID of measurement table
     */
    private void insertMeasurement(int ID, Measurement measurement, int measurementID) {
        String command = "INSERT INTO measurements VALUES(?, ?, ?, ?)";

        try (var stmt = this.connection.prepareStatement(command)) {

            stmt.setInt(1, ID);
            stmt.setTime(2, Time.valueOf(measurement.getTimeStart()));
            stmt.setTime(3, Time.valueOf(measurement.getTimeEnd()));
            stmt.setInt(4, measurementID);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }


    /**
     * Disconnects a measurement from a watch. The measurement Table will NOT be deleted
     *
     * @param ID Watch ID
     */
    public void removeMeasurementFromWatch(int ID) {
        String command = "DELETE FROM measurements WHERE ID = ?";
        int measurementID = getMeasurementID(ID);

        try (var stmt = this.connection.prepareStatement(command)) {
            stmt.setInt(1, ID);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        checkMeasurementClients(measurementID);
    }


    /**
     * Checks if the measurement has any connected watches left using it. If not it calls {@link DBManager#dropTable(String)}
     * To delete the dangling measurement table
     *
     * @param measurementID Measurement ID
     */
    private void checkMeasurementClients(int measurementID) {
        String command = "SELECT * FROM measurements WHERE measurement_ID = ?";

        if (measurementID < 0) {
            System.out.println("[DBManager#checkMeasurementClients] measurementID is invalid");
            return;
        }

        try (var stmt = this.connection.prepareStatement(command)) {
            stmt.setInt(1, measurementID);
            ResultSet rs = stmt.executeQuery();

            if (!rs.next()) {
                dropTable("measurement" + measurementID);
            }
            rs.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }


    /**
     * Gets the duration of a measurement
     *
     * @param measurementID Measurement ID
     * @return Duration as an Integer. -1 if measurement is not found
     */
    private List<LocalTime> getMeasurementTimes(int measurementID) {
        String command = "SELECT * FROM measurements WHERE measurement_ID = ?";
        List<LocalTime> times = new ArrayList<>();

        System.out.println(command + " -" + measurementID);
        try (var stmt = this.connection.prepareStatement(command)) {
            stmt.setInt(1, measurementID);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                times.add(rs.getTime("time_start").toLocalTime());
                times.add(rs.getTime("time_end").toLocalTime());
            }
            rs.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        System.out.println("Got start and end times: " + times.get(0) + ", " + times.get(1));
        return times;
    }


    /**
     * Gets the {@link Measurement} given its ID. Using {@link DBManager#getMeasurementTimes(int)}
     *
     * @param measurementID Measurement ID
     * @return The measurement
     */
    private Measurement getMeasurement(int measurementID) {
        String command = "SELECT * FROM measurement" + measurementID;
        Measurement measurement = new Measurement();
        List<Pair<String, Integer>> values = new ArrayList<>();

        try (var stmt = this.connection.prepareStatement(command)) {
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                var pair = new Pair<>(rs.getString(1), rs.getInt(2));
                values.add(pair);
            }
            rs.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        var times = getMeasurementTimes(measurementID);

        if (times.size() != 2) {
            System.out.println("ERROR: incorrect times amount");
            return null;
        }

        measurement.setSensors(values);
        measurement.setTimeStart(times.get(0));
        measurement.setTimeEnd(times.get(1));

        return measurement;
    }


    /**
     * Gets the measurement ID of the measurement on a watch
     *
     * @param ID Watch ID
     * @return ID of the measurement
     */
    private int getMeasurementID(int ID) {
        String command = "SELECT * FROM measurements WHERE ID = ?";
        int measurementID = -1;

        try (var stmt = this.connection.prepareStatement(command)) {
            stmt.setInt(1, ID);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                measurementID = rs.getInt("measurement_ID");
            }
            rs.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return measurementID;
    }


    /**
     * Get the measurement of a watch. Using {@link DBManager#getMeasurement(int)}
     *
     * @param ID Watch ID
     * @return The measurement. {@code null} if watch has no active measurement
     */
    Measurement getWatchMeasurement(int ID) {
        int measurementID = getMeasurementID(ID);
        System.out.println("Got measurement ID: " + measurementID);
        if (measurementID > -1) {
            return getMeasurement(measurementID);
        }
        return null;
    }


    /**
     * Adds a comment to a watch
     *
     * @param ID      Watch ID
     * @param comment {@link Comment} to be added
     */
    public void addComment(int ID, org.openjfx.Comment comment) {
        String command = "INSERT INTO comments VALUES(?, ?, ?, ?, ?)";

        try (var stmt = this.connection.prepareStatement(command)) {

            stmt.setInt(1, ID);
            stmt.setTime(2, Time.valueOf(comment.getStartingTime()));
            stmt.setTime(3, Time.valueOf(comment.getEndTime()));
            stmt.setString(4, comment.getCommentBody());
            stmt.setString(5, comment.getCommentType());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }


    /**
     * Gets a list of comments from a watch
     *
     * @param ID Watch ID
     * @return List of {@link Comment}
     */
    List<Comment> getComments(int ID) {
        String command = "SELECT * FROM comments WHERE ID = ?";
        List<org.openjfx.Comment> comments = new ArrayList<>();
        org.openjfx.Comment comment;

        try (var stmt = this.connection.prepareStatement(command)) {
            stmt.setInt(1, ID);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                comment = new org.openjfx.Comment();
                comment.setStartingTime(rs.getTime(2).toLocalTime());
                comment.setEndTime(rs.getTime(3).toLocalTime());
                comment.setCommentBody(rs.getString(4));
                comment.setCommentType(rs.getString(5));

                comments.add(comment);
            }
            rs.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return comments;
    }


    /**
     * Removes all the comments associated to a watch
     *
     * @param ID Watch ID
     */
    public void removeComments(int ID) {
        String command = "DELETE FROM comments WHERE ID = ?";

        try (var stmt = this.connection.prepareStatement(command)) {
            stmt.setInt(1, ID);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void close() throws IOException {
        try {
            this.connection.close();
        } catch (SQLException e) {
            throw new IOException(e);
        }
    }
}
