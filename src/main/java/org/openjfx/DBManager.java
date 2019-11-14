package org.openjfx;

import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import org.sqlite.*;

class DBManager {

    private Set<Integer> dataIDList = new LinkedHashSet<>();

    final DateTimeFormatter dateFormatter = DateTimeFormatter.BASIC_ISO_DATE;
    final DateTimeFormatter timeFormatter = DateTimeFormatter.ISO_LOCAL_TIME;

    private Connection connect(){
        Connection connection = null;
        String databaseLoc = "jdbc:sqlite:" + System.getProperty("user.dir") + "/database.sqlite";
        try{
            SQLiteConfig config = new SQLiteConfig();
            config.enforceForeignKeys(true);
            connection = DriverManager.getConnection(databaseLoc, config.toProperties());
            System.out.println("DB: Connected database");
        }catch (SQLException e){
            System.out.println(e.getMessage());
        }
        return connection;
    }


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


   /* private void createDataTable(int dataID){
        String command = "CREATE TABLE data" + dataID + "(datetime INTEGER, data_1 double NOT NULL, data_2 double, data_3 double, CONSTRAINT unique_time UNIQUE(date, time));";

        try{
            Connection con = connect();
            PreparedStatement stmt = con.prepareStatement(command);

            stmt.executeUpdate();
            stmt.close();
            con.close();
            System.out.println("DB: Created data" + dataID);
        }catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }*/

    private void insertSensor(int ID, String sensor, int dataListSize){
        insertSensor(ID, sensor, dataListSize, getNewDataID());
    }

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

   /* private boolean containsTable(String tableName){
        String command = "SELECT name FROM sqlite_master WHERE type='table' AND name=?";

        try{
            Connection con = connect();
            PreparedStatement stmt = con.prepareStatement(command);
            stmt.setString(1, tableName);
            ResultSet rs = stmt.executeQuery();

            if(!rs.next()){
                rs.close();
                stmt.close();
                con.close();
                System.out.println("DB: table " + tableName + " is NOT found");
                return false;
            }
            stmt.close();
            rs.close();
            con.close();
        }catch (SQLException e){
            System.out.println(e.getMessage());
        }
        System.out.println("DB: table " + tableName + " is found");
        return true;
    }*/

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

    int insertDatalist(int ID, SensorData dataList) {
        System.out.println("DB: insertDatalist");
        int dataID = getDatalistID(ID, dataList.getSensor());
        int size = dataList.getDataFieldsNumber();
        StringBuilder command;
        Connection con = null;

        if(dataID == -1){
            dataID = getNewDataID();
            insertSensor(ID, dataList.getSensor(), size, dataID);
        }

        try{
            con = connect();
            PreparedStatement stmt = null;
            con.setAutoCommit(false);
            System.out.println("DB: size of added list is " + dataList.size());
            for(int i = 0; i < dataList.size(); i++){
                LocalDate date = dataList.get(i).getDate();
                LocalTime time = dataList.get(i).getTime();
                String datetime = date.toString() + " " + time.toString();

                Double data1 = dataList.get(i).getDataList().get(0);
                command = new StringBuilder("INSERT INTO data" + dataID + "(datetime, data_1");


                for(int j = 1; j < size; j++){
                    command.append(",data_").append(j + 1);
                }
                command.append(") VALUES(?,?");
                command.append(",?".repeat(Math.max(0, size - 1)));
                command.append(")");

                System.out.println("DB: " + command + " VALUES: " + datetime.toString() + "," + data1);

                stmt = con.prepareStatement(command.toString());

                stmt.setString(1, datetime.toString());

                stmt.setDouble(2, data1);

                for (int j = 1; j < size; j++) {
                    stmt.setDouble(j+2, dataList.get(i).getDataList().get(j));
                }

                try{
                    int affected = stmt.executeUpdate();
                }catch (SQLException e){
                    if(e.getErrorCode() != 19){
                        throw e;
                    }else{
                        System.out.println("DB: duplicate filtered!");
                    }
                }


                /*if(affected != -1){
                    con.rollback();
                }*/
            }

            con.commit();
            System.out.println("DB: added watch with ID " + ID);

            if(stmt != null){
                stmt.close();
            }
            con.close();

        }catch (SQLException e){
            System.out.println(e.getMessage());
            try{
                con.rollback();
                con.close();
            }catch (SQLException e1){
                System.out.println(e.getMessage());
            }

            return 1;
        }
        return 0;
    }

    SensorData getDataList(int ID, String sensor){
        return getDataList(ID, sensor, LocalTime.MIN,LocalTime.MAX);
    }

    private SensorData getDataList(int ID, String sensor, LocalTime startTime, LocalTime endTime){
        int dataID = getDatalistID(ID, sensor);
        int columns = 0;
        String command = "SELECT * FROM data" + dataID + " WHERE datetime BETWEEN ? AND ?";
        int dataListSizeCount = 0;
        SensorData data = null;

        LocalTime time;
        LocalDate date;
        List<Double> dataList;

        System.out.println("DB: " + command);
        try{
            Connection con = connect();

            DatabaseMetaData md = con.getMetaData();
            ResultSet rs1 = md.getColumns(null, null, "data" + dataID, null);
            ResultSetMetaData rmd = rs1.getMetaData();
            for(int i = 1; i <= rmd.getColumnCount(); i++){
                if(rmd.getColumnName(i).contains("data_")){
                    dataListSizeCount++;
                }
            }

            data = new SensorData(ID, sensor, dataListSizeCount);
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
