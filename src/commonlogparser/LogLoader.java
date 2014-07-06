/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package commonlogparser;


import java.util.Date;
import java.util.List;


/**
 *
 * Класс для загрузки записи лога в БД
 */
public class LogLoader {
    
    
    private LogParser logParser;
    
    private  Date lastDateForCheck;
    
    private MySqlDB mySqlDB;
    
    
    public LogLoader(){
        logParser=new LogParser();
        mySqlDB=new MySqlDB();
    }
    
   public boolean doWorkLoading(){
       List<LogEntry> list=logParser.getNewLogEntries();
       if (list.size()==0) return false;
       // тут берем и сохраняем дату первого лога в этой итерации загрузки
       lastDateForCheck = list.get(0).getDateTime();
       mySqlDB.saveLog(list);
       return true;
   }
   
   
   public Date getLastDateForCheck(){
       return  lastDateForCheck;
   }
    
    

  

}
