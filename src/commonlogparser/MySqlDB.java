/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package commonlogparser;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * Работает с нашим классом в БД MySQL
 */
public final class MySqlDB {

    private Connection connection;
    
    
    private String dburl;
    
    private String dbuser;
    
    private String dbpassw;

    PreparedStatement insertStatement;

    PreparedStatement insertEventStatement;

    PreparedStatement selectLogsStm;
    
    PreparedStatement configGetStm;
    
    PreparedStatement configSetStm;

    String sql;

    static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public MySqlDB() {
        try {
            loadDataBaseSetting();
            configGetStm=getConnection().prepareStatement("select * from settings where key_col=?");
            configSetStm=getConnection().prepareStatement("update settings set value_col=? where key_col=?");
            selectLogsStm = getConnection().prepareStatement("select * from logs limit ?,?");
            insertStatement = getConnection().prepareStatement(
                    "INSERT INTO logs (ip,user_name,date_time,resource_url,status_code,response_size,user_agent) "
                    + "VALUES (?,?,?,?,?,?,?)");

            insertEventStatement = getConnection().prepareStatement("INSERT INTO events "
                    + "(event_type,date_time,ip_address,description) VALUES (?,?,?,?)");
        } catch (SQLException ex) {
            Logger.getLogger(MySqlDB.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            MyLogger.error("database properties reading failed",ex);
            Logger.getLogger(MySqlDB.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    
    private void loadDataBaseSetting() throws  IOException{
            Properties properties=new Properties();
            properties.load(new FileReader("db.config"));
            dburl=properties.getProperty("dburl");
            dbuser=properties.getProperty("dbuser");
            dbpassw=properties.getProperty("dbpassword");
    }
    
    
    public String getConfigValue(String keyName){
        try {
            configGetStm.setString(1, keyName);
            ResultSet result=configGetStm.executeQuery();
            result.next();
            String value=result.getString(2);
            return value;
        } catch (SQLException ex) {
            Logger.getLogger(MySqlDB.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public void setConfigValue(String key,String value){
        try {
            configSetStm.setString(1, value);
            configSetStm.setString(2, key);
            configSetStm.executeQuery();
        } catch (SQLException ex) {
            Logger.getLogger(MySqlDB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public List<LogEntry> getLogs(int offset, int limit) {
        LinkedList<LogEntry> list = new LinkedList<LogEntry>();
        try {
            selectLogsStm.setInt(1, offset);
            selectLogsStm.setInt(2, limit);
            ResultSet result = selectLogsStm.executeQuery();
            while (result.next()) {
                LogEntry log = new LogEntry();
                log.setId(result.getInt(1));
                log.setIp(result.getString(2));
                log.setUserName(result.getString(3));
                String date_str = result.getString(4);
                log.setDateTime(dateFormat.parse(date_str));
                log.setResourceUrl(result.getString(5));
                log.setStatus(result.getInt(6));
                log.setResponseSize(result.getInt(7));
                list.add(log);
            }
        } catch (Exception ex) {
            MyLogger.error("", ex);
            Logger.getLogger(MySqlDB.class.getName()).log(Level.SEVERE, null, ex);
        }
        return list;
    }

    public int getLogsCount() throws SQLException {
        Statement stm = getConnection().createStatement();
        ResultSet rs = stm.executeQuery("select count(*) from logs");
        rs.next();
        return rs.getInt(1);
    }

    public List<Event> getEvents() {
        LinkedList<Event> list = new LinkedList<Event>();
        try {

            Statement s = getConnection().createStatement();

            ResultSet result = s.executeQuery("select * from events order by date_time");
            while (result.next()) {
                Event e = new Event();
                e.setId(result.getInt(1));
                e.setEventType(result.getInt(2));
                String date_str = result.getString(3);
                e.setDateTime(dateFormat.parse(date_str));
                e.setIpAddress(result.getString(4));
                e.setDescr(result.getString(5));
                list.add(e);
            }

        } catch (Exception ex) {
            MyLogger.error("", ex);
        }
        return list;
    }

    public void saveLog(List<LogEntry> logs) {
        for (LogEntry l : logs) {
            try {
                insertStatement.setString(1, l.getIp());
                insertStatement.setString(2, l.getUserName());
                String time = dateFormat.format(l.getDateTime());
                insertStatement.setString(3, time);
                insertStatement.setString(4, l.getResourceUrl());
                insertStatement.setInt(5, l.getStatus());
                insertStatement.setInt(6, l.getResponseSize());
                insertStatement.setString(7, l.getUserAgent());
                insertStatement.executeUpdate();

            } catch (SQLException ex) {
                MyLogger.error("Insertin log entry to DB failed", ex);
            }
        }
    }

    public void saveEvent(Event event) {
        try {
            insertEventStatement.setInt(1, event.getEventType());
            String time = dateFormat.format(event.getDateTime());
            insertEventStatement.setString(2, time);
            insertEventStatement.setString(3, event.getIpAddress());
            insertEventStatement.setString(4, event.getDescr());
            insertEventStatement.executeUpdate();
        } catch (SQLException ex) {
            MyLogger.error("", ex);
        }
    }

    public int deleteLogs() throws SQLException {
        return getConnection().createStatement()
                .executeUpdate("delete from logs");
    }

    public Connection getConnection() {

        if (connection == null) {
            try {
                DriverManager.registerDriver(new com.mysql.jdbc.Driver());
                connection = DriverManager.getConnection(dburl, dbuser, dbpassw);
            } catch (SQLException ex) {
                MyLogger.error("DB connection establishing failed", ex);
                Logger.getLogger(MySqlDB.class.getName()).log(Level.SEVERE, null, ex);
                if (ex.getErrorCode() == 1049) {
                    MyLogger.warn("Database \"ApacheInspector\" does not exists ");
                }
            }
        }
        return connection;
    }
    
    

}
