/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package commonlogparser;

import java.io.IOException;
import org.apache.log4j.*;


public class MyLogger {

    private static Logger logger;
    
    private static Logger getLogger() {

        if (logger == null) {
            FileAppender appender;
            logger=Logger.getLogger("CommonLogParser");
        
//            PropertyConfigurator.configure("log4j.properties");
            
            try {
                appender = new FileAppender(new PatternLayout("%d %p %c: %m%n"),"logs.txt");
                BasicConfigurator.configure(appender);
            } catch (IOException ex) {
                java.util.logging.Logger.getLogger(MyLogger.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            }
        }
        return logger;

    }

    public static void info(String msg) {
        getLogger().info(msg);
    }

    public static void warn(String msg) {
        getLogger().warn(msg);
    }

    public static void debug(String msg) {
        getLogger().debug(msg);
    }

    public static void error(String msg, Exception e) {
        getLogger().error(msg, e);
    }

}
