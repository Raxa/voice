

package org.raxa.module.handlesms;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.raxa.alertmessage.MedicineInformation;
import org.raxa.alertmessage.MessageTemplate;
import org.raxa.database.FollowupResponse;
import org.raxa.database.FollowupQstn;
import org.raxa.database.Alert;
import org.raxa.database.FollowupChoice;
import org.raxa.database.HibernateUtil;
import org.raxa.database.IvrMsg;
import org.raxa.database.Patient;
import org.raxa.database.SmsMsg;
import org.raxa.database.SmsRecord;
import org.raxa.database.VariableSetter;
import org.raxa.registration.Register;


/**
 * This class handles all the interaction with Database.
 * @author atul
 *
 */
public class DatabaseService implements VariableSetter {
		

		private static Logger logger = Logger.getLogger(DatabaseService.class);
		
		
		private boolean isErrorWhileRetreiving=false;
		
		public DatabaseService(){}
		 
		/**
		 *  Return information of the drug which patient has to take between startTime to endTime for an alert of type alertType
		 *  <p> set isErrorWhileRetreiving to true if some error occur while receiving information
		 *  
		 * @param pid patientID
		 * @param today new Date()
		 * @param startTime startTime to fetch medicine Reminder
		 * @param endTime  endTime to fetch medicine Reminder
		 * @param alertType  ivr or sms
		 * @return List<MedicineInformation>
		 * @return null if some error occurs
		 */
		protected List<MedicineInformation> getMedicineInfo(String pid,Date today,Date startTime,Date endTime,int alertType){
			String hql;Date time1,time2;time1=null;time2=null;
			List<MedicineInformation> list=new ArrayList<MedicineInformation>();
			Session session=HibernateUtil.getSessionFactory().openSession();
			try{
			if(startTime==null && endTime==null && today==null)
				return null;
			else if(startTime==null && endTime==null){
				today.setHours(0);
				today.setMinutes(1);
				time1=today;
			}
			else if(startTime==null && !(endTime==null)){
				return null;
			}
			else if(endTime==null && startTime!=null){
				time1=startTime;
			}
			else{
				time1=startTime;time2=endTime;
			}
			session.beginTransaction();
			if(time2==null)
				hql="from Alert where scheduleTime>=:time1 and pid=:pid and alertType=:alertType order by scheduleTime";
			else
				hql="from Alert where scheduleTime>=:time1 and scheduleTime<=:time2 and pid=:pid and alertType=:alertType order by scheduleTime";
			Query query=session.createQuery(hql);
			query.setString("pid", pid);
			query.setTimestamp("time1",time1);
			query.setInteger("alertType",alertType);
			if(time2!=null)
				query.setTimestamp("time2",time2);
			
			String hql2=null;
			if(alertType==IVR_TYPE)
				hql2="from IvrMsg where ivrId=:msgId order by itemNumber";
			else if(alertType==SMS_TYPE)
				hql2="from SmsMsg where smsId=:msgId order by itemNumber";
			
			List<Alert> content=(List<Alert>)query.list();
			for(Alert alert: content){
				List<String> itemContents=new ArrayList<String>();
	    		Query query2=session.createQuery(hql2);
	    		query2.setInteger("msgId",alert.getMessageId());
	    		List<SmsMsg> results=(List<SmsMsg>)query2.list();	
	    		for(SmsMsg s:results){
	    			itemContents.add(s.getContent());
	    		}
	    			MedicineInformation m=new MessageTemplate().getMedicineInformation(itemContents);
	    			m.setScheduleTime(alert.getScheduleTime());
	    			list.add(m);
	    	}
			
			
			}
			catch(Exception ex){
				
				logger.info("Some error occured while in incoming-call and fetching information about patient with id "+pid);
				logger.error("\nCaused by:\n",ex);
				list=null;
				isErrorWhileRetreiving=true;
			}
			session.getTransaction().commit();
			session.close();
			return list;
		}
		
		/**
		 * 
		 * @param pid
		 * @param today
		 * @param startTime
		 * @param endTime
		 * @param alertType
		 * @return String that can will be sent as an SMS
		 */
		protected String getReminder(String pid,Date today,Date startTime,Date endTime,int alertType){
			List<MedicineInformation> info=getMedicineInfo(pid, today, startTime, endTime, alertType);
			if(info==null && isErrorWhileRetreiving)
				return RMessage.NOTHINGTOREPLY.getMessage();
			else
				return new MessageTemplate().getTextToconvertToSMSReminder(info);
		}
	  /**
	   * Get all patient who are registered for alert and bearing phone number(primary or secondary) as pnumber
	   * @param pnumber : phone number
	   * @return List<Patient>
	   */
	  protected static List<Patient> getAllRegisteredPatientWithNumber(String pnumber){
	    	List<Patient> nameAndId=new ArrayList<Patient>();
	    	try{
		    	Session session = HibernateUtil.getSessionFactory().openSession();
		    	session.beginTransaction();
		    	String hql="from Patient where (pnumber=:pnumber or snumber=:pnumber) and pid in (select pid from PAlert where alertType=:alertType)";
	    	Query query=session.createQuery(hql);
	    	query.setString("pnumber", pnumber);
	    	query.setInteger("alertType", SMS_TYPE);
	    	List<Patient> patientList=(List<Patient>)query.list();
	    	if(patientList==null || patientList.size()<1)
	    		return null;
	    	for(int i=0;i<patientList.size();i++){
	    		Patient p=(Patient)patientList.get(i);
	    		nameAndId.add(p);
	    	}
	    	
		}
		catch(Exception ex){
			logger.error("unable to retrieve data for patient with phone number:"+pnumber);
			logger.error("\nCaused by:\n",ex);
		    		nameAndId=null;
		    	}
		    	return nameAndId;
		    }
	  
		  /**
		   * <p>The list of patient is fetched from REST call to RAXAEMR</p>
		   * @param pnumber
		   * @param language
		   * @return List<Patient> Return all patient that are associated with a number.
		   */
		  protected  List<Patient> getAllUnRegisteredPatientWithNumber(String pnumber,String language){
			   return new Register().getpatientFromNumber(pnumber,language);
		  }
		/**
		 *  Update SMSResponse(maintains record of all the SMS Sent to the patient).
		 *  
		 * @param pnumber Number from which sms has received
		 * @param inDate  Time when SMS is receieved
		 * @param message  Query receieved
		 * @param reply  Reply message to the Query
		 * @param outDate  TIme at which message is sent to SMS Gateway
		 * @param isSuccess If the message is successfully sent to the Patient
		 * @param transID  transcation ID generated from the service provided by SMSGateway
		 */
		protected static void updateSMSResponse(String pnumber, Date inDate,
				String message, String reply, Date outDate, boolean isSuccess,
				String transID) {
			
			java.sql.Timestamp inT=new Timestamp(inDate.getTime());
			java.sql.Timestamp outT=new Timestamp(outDate.getTime());
			SmsRecord record=new SmsRecord(pnumber,inT,message,reply,outT,isSuccess,transID);
			Session session = HibernateUtil.getSessionFactory().openSession();
			session.beginTransaction();
			session.save(record);
			session.getTransaction().commit();
			session.close();
		}
		  
		  
		/**
		 * persist incoming sms
		 * 
		 */
		
	public static void saveIncomingSMS(String pnumber, String sysNumber, Date inDate, String message){
		try{
			logger.info("Persisting incoming sms");
			String sql="INSERT INTO incomingsms (sys_number,user_number,msgtxt) VALUES (?,?,?)";
			Session session = HibernateUtil.getSessionFactory().openSession();
	    	session.beginTransaction();
	    	SQLQuery query=session.createSQLQuery(sql);
	    	query.setString(0, sysNumber);
	    	query.setString(1, pnumber);
	    	query.setString(2, message);
	    	query.executeUpdate();
	    	session.getTransaction().commit();
	    	session.close();
		}catch(Exception ex){
			logger.info("Some error occured while fetching followupChoices");
			logger.error("\n ERROR Caused by\n",ex);
		}	
	}
	
  /**
   * Get all patient who are registered for alert and bearing phone number(primary or secondary) as pnumber
   * @param pnumber : phone number
   * @return List<Patient>
   */
  protected static Patient getPatientForFollowup(String pnumber, int fid){
	try{
    	Session session = HibernateUtil.getSessionFactory().openSession();
    	session.beginTransaction();
    	String hql="from Patient where (pnumber=:pnumber or snumber=:pnumber) and pid in (select pid from FollowupQstn where fid=:fid)";
    	Query query=session.createQuery(hql);
    	query.setString("pnumber", pnumber);
    	query.setInteger("fid", fid);
    	return (Patient) query.list().get(0);
    	}
	catch(Exception ex){
		logger.error("unable to retrieve data for followup patient with phone number:"+pnumber);
		logger.error("\nCaused by:\n",ex);
    	}
    	return null;
    }
  
	public static List<FollowupChoice> getFollowupChoices(int fid){
	    //Get followup choices
		List<FollowupChoice> followupChoices = null;
		try {
		Session session = HibernateUtil.getSessionFactory().openSession();
		session.beginTransaction();
	    String hqlChoice = "from FollowupChoice where fid=:fid";
	    Query queryChoice = session.createQuery(hqlChoice);
	    queryChoice.setInteger("fid", fid);
	    followupChoices = (List<FollowupChoice>) queryChoice.list();
		session.getTransaction().commit();
		session.close();
		} catch (Exception ex){
			logger.info("Some error occured while fetching followupChoices");
			logger.error("\n ERROR Caused by\n",ex);
		}
		return followupChoices;
	}
	
	public static boolean saveFollowupResponse (FollowupResponse followupResponse){
	try {
		Session session = HibernateUtil.getSessionFactory().openSession();
		session.beginTransaction();
	    session.save(followupResponse);
		session.getTransaction().commit();
		session.close();
		return true;
		} catch (Exception ex){
			logger.info("Some error occured while fetching followupChoices");
			logger.error("\n ERROR Caused by\n",ex);
			return false;
		}
	}


  
}