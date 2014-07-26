
package org.raxa.scheduler;

import java.util.List;
import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.raxa.alertmessage.MessageTemplate;
import org.raxa.database.Alert;
import org.raxa.database.HibernateUtil;
import org.raxa.database.Patient;
import org.raxa.database.VariableSetter;

/**
 * Call SMSModule and Provide Info which will then send message to patient
 * @author atul
 *
 */
public class Messager implements Runnable,VariableSetter {

	private AlertInfo patient;
	private FollowupQstn followupQstn;
	private int SMS_TYPE;
	
	public Messager(AlertInfo patient){
		this.patient=patient;
		this.SMS_TYPE = ALERT_TYPE;
	}
	
	public Messager(FollowupQstn followupQstn){
		this.followupQstn = followupQstn;
		this.SMS_TYPE = FOLLOWUP_TYPE;
	}
	
	public void run(){
		Logger logger=Logger.getLogger(this.getClass());
		String message="";
		if(this.SMS_TYPE == ALERT_TYPE)
		{
			message = getMessageContent(patient.getMsgId());
			updateAlertCount();
			logger.debug("Sending \n message:"+message+"\n Phone Number:"+patient.getPhoneNumber());
			new SMSManager(patient).sendSMS(message);
		}
		else if(this.SMS_TYPE == FOLLOWUP_TYPE)
		{
			//TODO: Get actual phonenumbers
			String pnumber = "SIP/1000abc";
			message = followupQstn.getQstn();
			new SMSManager().sendSMS(pnumber,message);
		}	
		
	}
	/**
	 * Get message to be send given a message ID
	 * @param msgId
	 * @return message to send as an SMS
	 */
	public String getMessageContent(int msgId){
		return new MessageTemplate().getTexttoSMS(getContentFromDB(msgId));
	}
	/**
	 * Given a msgID returns all the message associated with it
	 * @param msgId
	 * @return List<String> all messages in a serial order 
	 */
	private List<String> getContentFromDB(int msgId){
		Logger logger=getLog();
		logger.debug("Getting content for medicine Reminder haveing msgId"+msgId);
		try{
			String hql="select content from SmsMsg where smsId=:msgId order by itemNumber";
			Session session = HibernateUtil.getSessionFactory().openSession();
	    	session.beginTransaction();
	    	Query query=session.createQuery(hql);
	    	query.setInteger("msgId", msgId);
	    	List<String> content=(List<String>) query.list();
	    	session.getTransaction().commit();
	    	session.close();
	    	logger.debug("Successfully retreived msg content from database with msgId"+msgId);
	    	return content;
		}
		catch(Exception ex){
			logger.info("Some error occured while fetching content from msgId:"+msgId);
			logger.error("\n ERROR Caused by\n",ex);
		}
		return null;
	}
	/**
	 *
	 * @return logger
	 */
	private Logger getLog(){
		Logger logger = Logger.getLogger(this.getClass());
		return logger;
	}
	
	
	/**
	 * Update retryCount.
	 */
	public void updateAlertCount(){
		Session session = HibernateUtil.getSessionFactory().openSession();
		session.beginTransaction();
		String hql="from Alert where aid=:aid and alertType=:alertType";
		Query query=session.createQuery(hql);
		query.setString("aid", patient.getAlertId());
		query.setInteger("alertType", SMS_TYPE);
		Alert alert = (Alert) query.list().get(0);
		int retryCount=alert.getretryCount()+1;
		alert.setretryCount(retryCount);
		session.update(alert);
		session.getTransaction().commit();
		session.close();
	}
}


