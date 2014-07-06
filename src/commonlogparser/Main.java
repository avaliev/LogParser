/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package commonlogparser;

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Точка входа. В итоге будет 3 таблицы : Логи, События, Типы событий
 */
public class Main {

    public static void main(String[] args) {
        try {
            MySqlDB db = new MySqlDB();
            LogLoader logLoader = new LogLoader();
            LogAnalyzer analyzer = new LogAnalyzer();
            while (true) {
                boolean hasNew = logLoader.doWorkLoading();
                int mlsec = Integer.parseInt(db.getConfigValue("check_period"));
                if (hasNew) {
                    Date date = logLoader.getLastDateForCheck();
                    analyzer.checkLogs(date);
                    MyLogger.info("Success cycle");
                    System.out.println("Success cycle");
                }
                Thread.sleep(mlsec * 1000);
            }

        } catch (Exception ex) {
            MyLogger.error("Exception:", ex);
            System.out.println(ex.toString());
        }
    }

    public static void testLogAnalyzer() {
        LogAnalyzer logAnalyzer = new LogAnalyzer();
        String checkDate = "14/Apr/2014:19:19:43 +0400";
        Date date;
        try {
            date = LogParser.dateFormat.parse(checkDate);
            logAnalyzer.checkLogs(date);
        } catch (Exception ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
