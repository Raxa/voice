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
 * This class sets all thread each containing information about which patient to be called,what message to play etc.It pass AlertInfo Object for alert.
 * @author atul
 *
 */
public class AlertSetter implements Runnable,VariableSetter{
		static Logger logger = Logger.getLogger(AlertSetter.class);
		private Date today;
		
		public AlertSetter(Date today){
			this.today=today;
		}
	
		public void run(){
			if(!isSameDay()){				//change today when new day occurs
				resetDatabase();
				today=new Date();
			}	
		
		List<AlertInfo> listOfIVRCaller=(new GetAlertInfo()).getPatientInfoOnTime(new Date(),IVR_TYPE);
		List<AlertInfo> listOfSMSCaller=(new GetAlertInfo()).getPatientInfoOnTime(new Date(),SMS_TYPE);
		logger.info("SMS size = "+listOfSMSCaller.size());
		logger.info("IVR size = "+listOfIVRCaller.size());
			
		if(listOfIVRCaller!=null)
			setIVRThread(listOfIVRCaller);
		else logger.debug("In CallSetter:run-No IVRTuple found for the next interval");
		
		if(listOfSMSCaller!=null)
			setSMSThread(listOfSMSCaller);
		else logger.debug("In CallSetter:run-No SMSTuple found for the next interval");
		
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
	 * If Database is getting updated by "Updater Service" This method should be removed.
	 * 
	 * Update the database and set isExecuted to no and retry_count to 0 each day(at midnight).
	 */
	public void resetDatabase(){
		  ReminderUpdate r=new ReminderUpdate();
		  Session session = HibernateUtil.getSessionFactory().openSession();
		  session.beginTransaction();
		  String hql="select p.pname,p.preferLanguage,pa.pid,pa.alertType from Patient p,PAlert pa where p.pid=pa.pid";
		  Query query=session.createQuery(hql);
		  Iterator results=query.list().iterator();
		  session.getTransaction().commit();
		  session.close();
		  try{
				Object[] row=(Object[]) results.next();
				
				while(true){
					try{
						  String pname=(String) row[0];
						  String preferLanguage=(String) row[1];
						  String pid=(String) row[2];
						  int alertType=(Integer) row[3];
						  r.resetReminder(pid, pname, preferLanguage, alertType);
						  							//System.out.println(pid+"\n"+pname+"\n"+preferLanguage+"\n"+alertType);
						  if(results.hasNext())
							  row=(Object[]) results.next();
						  else break;
					}
					catch(Exception ex){
						logger.error("Some error while trying to reset Reminder of a patient");
						logger.error("\nCaused by:\n",ex);
					}
				}
			}
			catch(Exception ex){
				logger.error("unable to get patientList on ");
				logger.error("\nCaused by:\n",ex);
			}
	}
	
	/**
	 * Set threads which will call patient
	 * @param list
	 */
	
	public void setIVRThread(List<AlertInfo> list){
		Properties prop = new Properties();
		int THREAD_POOL_CALLER=50;
		try{
			prop.load(AlertSetter.class.getClassLoader().getResourceAsStream("config.properties"));
			THREAD_POOL_CALLER=Integer.parseInt(prop.getProperty("Thread_Pool_Caller"));
		}
		catch (IOException ex) {
			logger.error("\nERROR while setting getting value from property file \n Caused by:\n",ex);
		}
		
		ScheduledExecutorService executor = Executors.newScheduledThreadPool(THREAD_POOL_CALLER);
		int count=0;
	    while(count<list.size()){
	    	AlertInfo a;
			a=list.get(count);
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
	    	AlertInfo a;
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

}
		
		
		
		
		
		
		
		
		
	
