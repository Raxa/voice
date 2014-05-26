

package org.raxa.scheduler;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.Date;
import java.io.IOException;
import java.util.Properties;
import org.apache.log4j.Logger;

/**
 * This Class will ping database after every two mins(DATABASE_PINGING_INTERVAL) and schedulgie all thread to call 
 * patient where patient scheduleTime falls between that time Zone.Pinging time can be changed easily 
 * by changing DATABASE_PINGING_INTERVAL 
 * 
 */
public class DatabasePoller{

	static Logger logger = Logger.getLogger(DatabasePoller.class);
	
	 public void start(){
		Properties prop = new Properties();
		int THREAD_POOL_DATABASE=1;int DATABASE_POLLING_INTERVAL=2;
    	try {
            prop.load(DatabasePoller.class.getClassLoader().getResourceAsStream("config.properties"));
    		THREAD_POOL_DATABASE=Integer.parseInt(prop.getProperty("Thread_Poll_Database"));
    		DATABASE_POLLING_INTERVAL=Integer.parseInt(prop.getProperty("Database_Poll_Interval"));
    	} 
    	catch (IOException ex) {
    		logger.error("Unable to set Thread.Some error in config.properties");
    		logger.error("\nCaused by\n",ex);
    	}
		ScheduledExecutorService executor = Executors.newScheduledThreadPool(THREAD_POOL_DATABASE);
		Runnable alertSetter = new AlertSetter(new Date());
		try{
			executor.scheduleWithFixedDelay(alertSetter,0,DATABASE_POLLING_INTERVAL,TimeUnit.SECONDS);
		}
		catch(Exception e){
			logger.error("\nCaused by\n",e);
		 }
	} 
}
