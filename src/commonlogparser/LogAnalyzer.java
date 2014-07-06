/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package commonlogparser;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;
import java.util.LinkedList;

/**
 *
 * Класс для анализа логов из главной таблицы, которая записывает в таблицу
 * подозрительных событий
 */
public class LogAnalyzer {

    int max_req_count;

    // получает логи после некоторой даты,
    // у которых с одного и того же адреса было больше нормы запросов в одну и ту же секунду
    String overloadPerIPSql = "SELECT ip,date_time,count(*) from logs where date_time>? group by ip,date_time having count(*)>?;";

    String overloadSql = "select date_time,count(*) from logs group by date_time having date_time>? and count(*)>?;";

    PreparedStatement overloadPerIPStm;

    PreparedStatement overloadStm;

    MySqlDB db;

    // чтобы в каждом новом периоде работы не учитывались предыдущие логи, сохраняем время завершения работы
    Date last_time;

   
    public LogAnalyzer() {
        try {
            db= new MySqlDB();
            overloadPerIPStm = db.getConnection().prepareStatement(overloadPerIPSql);
            overloadStm = db.getConnection().prepareStatement(overloadSql);
        } catch (SQLException ex) {
            MyLogger.error("", ex);
        }
    }

    public void checkTotalOverload(Date afterTime) throws SQLException, ParseException {
        String datetime = MySqlDB.dateFormat.format(afterTime);
        overloadStm.setString(1, datetime);
        String str=db.getConfigValue("max_req_per_second");
        overloadStm.setInt(2, Integer.parseInt(str));
        ResultSet resultSet = overloadStm.executeQuery();
        LinkedList<Event> list = new LinkedList<>();
        while (resultSet.next()) {
            Event e = new Event();
            e.setEventType(Event.OVERLOAD);
            String date_str = resultSet.getString(1);
            e.setDateTime(MySqlDB.dateFormat.parse(date_str));
            e.setDescr("Number of requests:" + resultSet.getInt(2));
            list.add(e);
        }

        if (list.size() > 0) {
            for (Event event : list) {
                db.saveEvent(event);
            }
        }
    }

    public void checkSuspiciousIP(Date afterTime) throws SQLException, ParseException {

        String datetime = MySqlDB.dateFormat.format(afterTime);
        overloadPerIPStm.setString(1, datetime);
        String s=db.getConfigValue("max_req_for_ip");
        overloadPerIPStm.setInt(2, Integer.parseInt(s));
        ResultSet resultSet = overloadPerIPStm.executeQuery();
        LinkedList<Event> list = new LinkedList<>();
        while (resultSet.next()) {
            Event e = new Event();
            e.setEventType(Event.SUSPICION_ADDRESS);
            e.setIpAddress(resultSet.getString(1));
            String date_str = resultSet.getString(2);
            e.setDateTime(MySqlDB.dateFormat.parse(date_str));
            e.setDescr("Number of requests:" + resultSet.getInt(3));
            list.add(e);
        }

        if (list.size() > 0) {
            for (Event event : list) {
                db.saveEvent(event);
            }
        }

    }

    //нужно проверять только следующие порции логов, не захватив уже проанализированные
    public void checkLogs(Date afterTime) {
        try {
            checkTotalOverload(afterTime);
            checkSuspiciousIP(afterTime);
        } catch (Exception ex) {
            MyLogger.error("", ex);
        }

    }

}
