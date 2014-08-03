package org.raxa.scheduler;

import org.raxa.database.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Date;
import java.util.ArrayList;
import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import java.io.IOException;
import java.util.Properties;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

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
			logger.info("In FollowupSetter");
		List<FollowupQstn> listOfIVRCaller = new ArrayList<FollowupQstn>();
		//List<FollowupQstn> listOfIVRCaller = getPatientsList(IVR_TYPE);
		List<FollowupQstn> listOfSMSCaller = getPatientsList(IVR_TYPE);
		
		/*
		FollowupQstn fq = new FollowupQstn();
		String	message = "Did you have your morning medication?\n1. Yes\n2. No \nType <###> (space)(your option)to send your option";
		fq.setQstn(message);
		List<FollowupQstn> listOfIVRCaller = new ArrayList<FollowupQstn>();
		List<FollowupQstn> listOfSMSCaller = new ArrayList<FollowupQstn>();	
		listOfSMSCaller.add(fq);*/
		logger.info("SMS size = "+listOfSMSCaller.size());
		logger.info("IVR size = "+listOfIVRCaller.size());
			
		if(listOfIVRCaller!=null)
			setIVRThread(listOfIVRCaller);
		else logger.info("In FollowupSetter:run-No IVRTuple found for the next interval");
		
		if(listOfSMSCaller!=null)
			setSMSThread(listOfSMSCaller);
		else logger.info("In FollowupSetter:run-No SMSTuple found for the next interval");
		
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
	public void setSMSThread(List<FollowupQstn> list){
		logger.info("Received "+list.size()+" SMS to send");
		Properties prop = new Properties();
		int THREAD_POOL_MESSAGER=50;
		try{
			prop.load(FollowupSetter.class.getClassLoader().getResourceAsStream("config.properties"));
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
	List<FollowupQstn> qstnsToAsk = new  ArrayList<FollowupQstn>();
	try{
		Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
		String hqlQstn = "from FollowupQstn where followupType=:followupType";// and scheduleTime<=:systemTime and toDate<=:toDate";
        Query queryQstn = session.createQuery(hqlQstn);
        queryQstn.setInteger("followupType", followupType);
		//queryQstn.setTimestamp("toDate", new Timestamp(new Date().getTime()));
		 List<FollowupQstn> qstns =  queryQstn.list();
		//Check if the question has already been asked today
		for(FollowupQstn fq: qstns){
			Query query = session.createQuery("from FollowupResponse where fid = :fid and date between :from and :to");
			query.setParameter("fid", fq.getFid());
			String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
			Date fromDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date+" 00:00:00");
			Date toDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date+"  23:59:59");
			query.setParameter("from", fromDate);
			query.setParameter("to", toDate);
			List list = query.list();
			//havent been asked
			if(list.size() == 0){
				qstnsToAsk.add(fq);
			}
		}
		session.getTransaction().commit();
        session.close();
	} catch(Exception ex){
		ex.printStackTrace();
	}	
	return qstnsToAsk;
	}
	
	public void resetDatabase(){
		//Todo
	}

}
		
		
		
		
		
		
		
		
		
	
