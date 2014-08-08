
package org.raxa.scheduler;

import java.util.List;
import java.math.BigInteger;
import org.apache.log4j.Logger;
import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.raxa.alertmessage.MessageTemplate;
import org.raxa.database.Alert;
import org.raxa.database.HibernateUtil;
import org.raxa.database.Patient;
import org.raxa.database.VariableSetter;
import org.raxa.database.FollowupChoice;
import org.raxa.database.FollowupQstn;

/**
 * Call SMSModule and Provide Info which will then send message to patient
 * @author atul
 *
 */
public class Messager implements Runnable,VariableSetter {

	private AlertInfo patient;
	private FollowupQstn followupQstn;
	private int SMS_TYPE;
	Logger logger=Logger.getLogger(this.getClass());
	private static String FOLLOWUP_KEYWORD = "FWP";

	
	public Messager(AlertInfo patient){
		this.patient=patient;
		this.SMS_TYPE = ALERT_TYPE;
	}
	
	public Messager(FollowupQstn followupQstn){
		this.followupQstn = followupQstn;
		this.SMS_TYPE = FOLLOWUP_TYPE;
	}
	
	public void run(){
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
			String pnumber = "9160741100";
			message = followupQstn.getQstn();
			int count = 0;
			for(FollowupChoice followupChoice: getFollowupChoices(followupQstn.getFid())){
				count++;
				message += "\n"+count+". "+followupChoice.getChoice();
			}
			int followupCacheId = getCacheId(followupQstn.getFid());
			message += "\n"+ "Type "+FOLLOWUP_KEYWORD+" "+followupCacheId+" (your option)to send your option.";
			new SMSManager(followupQstn).sendSMS(pnumber,message);
		}	
		
	}
	private int getCacheId(int fid) {
		int cacheId = 0;
		try{
			String sql="INSERT INTO followupcache (fid) VALUES (?)";
			Session session = HibernateUtil.getSessionFactory().openSession();
	    	session.beginTransaction();
	    	SQLQuery query=session.createSQLQuery(sql);
	    	query.setInteger(0, fid);
	    	query.executeUpdate();
	    	sql = "SELECT LAST_INSERT_ID() FROM followupcache";
	    	BigInteger result = (BigInteger) session.createSQLQuery(sql).uniqueResult();
		cacheId = result.intValue();
	    	session.getTransaction().commit();
	    	session.close();
		}catch(Exception ex){
			logger.info("Some error occured while fetching followupChoices");
			logger.error("\n ERROR Caused by\n",ex);
		}
		return cacheId;
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
	
private List<FollowupChoice> getFollowupChoices(int fid){
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


