package org.raxa.service.updater;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.Date;
import java.io.IOException;
import java.util.Properties;
import org.apache.log4j.Logger;

/**
 * Checks every pollingHours Hours if the day has changed or not.If it has changed then it will update the database.
 * @author Atul Agrawal
 *
 */
public class Updater{

	private Logger logger = Logger.getLogger(Updater.class);
	
	 public void start(){
		 Properties prop = new Properties();
		 int THREAD_POOL_DATABASE=3;int DATABASE_POLLING_INTERVAL=3;
		 try {
	            prop.load(Updater.class.getClassLoader().getResourceAsStream("config.properties"));
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
			executor.scheduleWithFixedDelay(alertSetter,0,DATABASE_POLLING_INTERVAL,TimeUnit.HOURS);
		}
		catch(Exception e){
		   logger.error("IMPORTANT:Unable to execute Scheduler at time:"+new Date());
		   
		 }
		
	} 
	 
	 
}
