package org.openjfx;

import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

public class DBManager {

    private static String databaseLoc = "jdbc:sqlite:B:/Javafx/watch-host/database.sqlite";

    private Set<Integer> dataIDList = new LinkedHashSet<>();

    private Connection connect(){
        Connection connection = null;
        try{
            connection = DriverManager.getConnection(databaseLoc);
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


    public void insertSensor(int ID, String sensor){
        //int dataID = 1;
        int dataID = getNewDataID();

        String command = "INSERT INTO datalists(ID, sensor_name, data_ID) VALUES(?, ?, ?)";

        try{
            Connection con = connect();
            PreparedStatement stmt = con.prepareStatement(command);

            stmt.setInt(1, ID);
            stmt.setString(2, sensor);
            stmt.setInt(3, dataID);
            stmt.executeUpdate();
            System.out.println("DB: added sensor " + sensor + " to watch " + ID);

            command = "create table data" + dataID + "(date DATE not null, time TIME not null, data_1 double not null, data_2 double, data_3 double);";
            stmt = con.prepareStatement(command);
            stmt.executeUpdate();

            System.out.println("DB: added a datalist with dataID " + dataID);

            stmt.close();
            con.close();
        }catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }

    void insertWatch(int ID){
        String command = "INSERT INTO smartwatch(ID) VALUES(?)";

        try{
            Connection con = connect();
            PreparedStatement stmt = con.prepareStatement(command);


            stmt.setInt(1, ID);
            //stmt.setString(2, "NONAME");
            stmt.executeUpdate();

            System.out.println("DB: added watch with ID " + ID);

            stmt.close();
            con.close();

        }catch (SQLException e){
            System.out.println(e.getMessage());
        }

        /*for(String sensor : sensorList){
            insertSensor(ID, sensor);
        }*/
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

    private boolean containsTable(String tableName){
        String command = "SELECT name FROM sqlite_master WHERE type='table' AND name=?";

        try{
            Connection con = connect();
            PreparedStatement stmt = con.prepareStatement(command);
            stmt.setString(1, tableName);
            ResultSet rs = stmt.executeQuery();

            if(rs == null){
                stmt.close();
                rs.close();
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
    }

    private int getDataListID(int ID, String sensor){
        String command = "SELECT data_ID FROM datalists WHERE ID=? AND sensor_name=?";
        int dataID = -1;

        try{
            Connection con = connect();
            PreparedStatement stmt = con.prepareStatement(command);
            stmt.setInt(1, ID);
            stmt.setString(2, sensor);
            ResultSet rs = stmt.executeQuery();
            dataID = rs.getInt("data_ID");

            stmt.close();
            rs.close();
            con.close();
        }catch (SQLException e){
            System.out.println(e.getMessage());
        }
        System.out.println("DB: found dataID " + dataID);
        return dataID;
    }

    void insertDatalist(int ID, SensorData dataList){
        int dataID = getDataListID(ID, dataList.getSensor());
        StringBuilder command;
        int size = dataList.get(0).getDataList().size();

        try{
            Connection con = connect();
            PreparedStatement stmt;
            System.out.println("DB: size of added list is " + dataList.size());
            for(int i = 0; i < dataList.size(); i++){
                LocalDate date = dataList.get(i).getDate();
                LocalTime time = dataList.get(i).getTime();
                Double data1 = dataList.get(i).getDataList().get(0);
                command = new StringBuilder("INSERT INTO data" + dataID + "(date, time, data_1");


                for(int j = 1; j < size; j++){
                    command.append(",data_").append(j + 1);
                }
                command.append(") VALUES(?,?,?");
                command.append(",?".repeat(Math.max(0, size - 1)));
                command.append(")");

                System.out.println("DB: " + command);

                stmt = con.prepareStatement(command.toString());

                stmt.setDate(1, Date.valueOf(date));
                stmt.setTime(2, Time.valueOf(time));
                stmt.setDouble(3, data1);

                for (int j = 1; j < size; j++) {
                    stmt.setDouble(j+3, dataList.get(i).getDataList().get(j));
                }
                //stmt.setString(2, "NONAME");
                stmt.executeUpdate();
                stmt.close();
            }

            System.out.println("DB: added watch with ID " + ID);


            con.close();

        }catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }

}
