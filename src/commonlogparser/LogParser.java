/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package commonlogparser;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

/**
 * Разбирает логи, сохраняя номер последней обработанной строки
 *
 */
public class LogParser {

    static final String DATE_FORMAT = "dd/MMM/yyyy:hh:mm:ss Z";

    static SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT,Locale.ENGLISH);
    
    
    static final int MAX_OPERATION_COUNT=1000;

    RandomAccessFile accessFile;
    
    private MySqlDB db;

    public LogParser() {
        try {
            db=new MySqlDB();
            accessFile = new RandomAccessFile(db.getConfigValue("apache_logfile_path"), "r");
        } catch (FileNotFoundException ex) {
            MyLogger.warn("apache log file not found");
        }
    }

    
    /**
     *  парсит логи пока не достигнут конец файла или некоторое максимальное число.
     * @return объекты модели логов
     */
    public List<LogEntry> getNewLogEntries() {
        LinkedList<LogEntry> list = new LinkedList<>();
        String line;
        int i=0;
        try {
            while ((line = accessFile.readLine()) != null && i<MAX_OPERATION_COUNT) {
                try {
                    list.add(convert(line));
                } catch (ParseException ex) {
                    MyLogger.error("line: " + i, ex);
                }
                i++;
            }
        } catch (IOException ex) {
            MyLogger.error("Error reading log file", ex);
        }
        return list;
    }

    private LogEntry convert(String line) throws ParseException {

        LogEntry log = new LogEntry();
        int pos = 0;
        // до первого пробела - IP адрес клиента
        int i = line.indexOf(' ');
        pos = i;
        log.setIp(line.substring(0, i));
        //  далее имя авторизованного пользователя (если есть)

        i = line.indexOf(' ', pos);

        pos = line.indexOf(' ', i + 1);

        log.setUserName(line.substring(i + 1, pos));
        pos = line.indexOf('[', pos);;
        i = line.indexOf(']', pos);

        String timeStr = line.substring(pos + 1, i);
        
        log.setDateTime(dateFormat.parse(timeStr));
        pos = i + 1;

        i = line.indexOf("\"", pos);
        if (i < 0) {
            i = line.indexOf("\"POST", pos);
        }
        pos = i + 1;

        i = line.indexOf("HTTP");

        log.setResourceUrl(line.substring(pos, i-1));
        // код HTTP ответа
        pos = line.indexOf('\"', pos) + 2;

        log.setStatus(Integer.parseInt(line.substring(pos, pos + 3)));
        pos = pos + 4;

        i = line.indexOf(' ', pos);
        log.setResponseSize(Integer.parseInt(line.substring(pos, i-1)));
        pos = i; // тут пробел

//        pos = line.indexOf('\"', pos);
//        pos = line.indexOf('\"', pos);
//        pos = line.indexOf('\"', pos); // перед user_agent;
//
//        i = line.indexOf('\"', pos + 1);
//
//        log.setUserAgent(line.substring(pos + 1, i));

        return log;
    }

    void p(int p) {
        System.out.print(p + " ");
    }
}
