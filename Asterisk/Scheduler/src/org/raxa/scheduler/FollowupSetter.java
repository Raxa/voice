package org.raxa.scheduler;

import org.raxa.database.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Date;
import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import java.io.IOException;
import java.util.Properties;

/**
 * This class sets all thread each containing information about which patient to be called,what message to play etc.It passes FollowupQstn Object for followup.
 * @author rahul
 *
 */
public class FollowupSetter implements Runnable,VariableSetter{
		static Logger logger = Logger.getLogger(FollowupSetter.class);
		private Date today;
		
		public FollowupSetter(Date today){
			this.today=today;
		}
	
		public void run(){
			if(!isSameDay()){				//change today when new day occurs
				resetDatabase();
				today=new Date();
			}	
		
		List<FollowupQstn> listOfIVRCaller = getPatientsList(IVR_TYPE);
		List<FollowupQstn> listOfSMSCaller = getPatientsList(SMS_TYPE);
		
		if(listOfIVRCaller!=null)
			setIVRThread(listOfIVRCaller);
		else logger.debug("In FollowupSetter:run-No IVRTuple found for the next interval");
		
		if(listOfSMSCaller!=null)
			setSMSThread(listOfSMSCaller);
		else logger.debug("In FollowupSetter:run-No SMSTuple found for the next interval");
		
	}
	/**
	 * Checks if the day is changed
	 * @return
	 */
	public boolean isSameDay(){
		Calendar cal1 = Calendar.getInstance();
		Calendar cal2 = Calendar.getInstance();
		cal1.setTime(today);
		cal2.setTime(new Date());
		boolean sameDay = cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
		                  cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH)&&
		                  cal1.get(Calendar.DAY_OF_MONTH)==cal2.get(Calendar.DAY_OF_MONTH);
		return sameDay;
	}
	
	/**
	 * Set threads which will call patient
	 * @param list
	 */
	
	public void setIVRThread(List<FollowupQstn> list){
		Properties prop = new Properties();
		int THREAD_POOL_CALLER=50;
		try{
			prop.load(FollowupSetter.class.getClassLoader().getResourceAsStream("config.properties"));
			THREAD_POOL_CALLER=Integer.parseInt(prop.getProperty("Thread_Pool_Caller"));
		}
		catch (IOException ex) {
			logger.error("\nERROR while setting getting value from property file \n Caused by:\n",ex);
		}
		
		ScheduledExecutorService executor = Executors.newScheduledThreadPool(THREAD_POOL_CALLER);
		int count=0;
	    while(count<list.size()){
	    	FollowupQstn a;
			a=list.get(count);
			//TODO: Add support for regional languages
			Caller caller=new Caller(a);
			try{
				executor.schedule(caller,0,TimeUnit.SECONDS);
			}
			catch(Exception ex){
				logger.error("In function setIVRThread:Error Occured");
				logger.error("\nCaused by\n",ex);
			}
			finally{
				count++;
			}
		}
	}
	
	/**
	 * Set all the threads which will message patient
	 * @param list
	 */
	public void setSMSThread(List<AlertInfo> list){
		Properties prop = new Properties();
		int THREAD_POOL_MESSAGER=50;
		try{
			prop.load(AlertSetter.class.getClassLoader().getResourceAsStream("config.properties"));
			THREAD_POOL_MESSAGER=Integer.parseInt(prop.getProperty("Thread_Pool_Messager"));
		}
		catch (IOException ex) {
			logger.error("\nERORR while setting getting value from property file \n Caused by:\n",ex);
			logger.error("\nCaused by\n",ex);
        }
		
		ScheduledExecutorService executor = Executors.newScheduledThreadPool(THREAD_POOL_MESSAGER);
		int count=0;
	    while(count<list.size()){
	    	FollowupQstn a;
			a=list.get(count);
			Messager sMSSender=new Messager(a);
			try{
				executor.schedule(sMSSender,1,TimeUnit.SECONDS);
			}
			catch(Exception ex){
				logger.error("In function setSMSThread:Error Occured");
				logger.error("\nCaused by\n",ex);
			}
			finally{
				count++;
			}
		}
	}
	
	public List<FollowupQstn> getPatientsList(int followupType)
	{
		String hqlQstn = "from FollowupQstn where followupType=:followupType and a1.scheduleTime<=:systemTime and toDate<=:toDate";
        Query queryQstn = session.createQuery(hqlQstn);
        queryQstn.setInteger("followupType", followupType);
		queryQstn.setInteger("toDate", new Date());
        return (FollowupQstn) queryQstn.list();
	}

}
		
		
		
		
		
		
		
		
		
	
