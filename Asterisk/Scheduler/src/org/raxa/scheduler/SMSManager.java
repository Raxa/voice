package org.raxa.scheduler;

import java.util.Date;
import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.raxa.database.HibernateUtil;
import org.raxa.database.VariableSetter;
import org.raxa.module.sms.SMSResponse;
import org.raxa.module.sms.SMSSender;

/**
 * Handle all request which needs to send SMS.
 * @author atul
 *
 */
class SMSManager implements VariableSetter{
	
	private AlertInfo patient;
	private static final String senderID="TEST SMS";
	/**
	*phone number of patient
	*/
	private phoneNumber;
	SMSManager(AlertInfo ai){
		patient=ai;
	}
	
	SMSManager(){
	}
	/**
	 * Call SMSSender to send message to patient
	 * @param message
	 */
	public void sendSMS(String message){
		Logger logger = Logger.getLogger(this.getClass());
		String username="foo";			
		String password="bar";
		SMSSender sender=new SMSSender();
		if(!(sender.login(username,password))){
				logger.error("IMPORTANT:Cannot loggin to Send SMS for username:"+username+" password:"+password);
				return;
		}
		
		SMSResponse response=sender.sendSMSThroughGateway(message, patient.getPhoneNumber(),senderID,patient.getpreferLanguage());
		if(response.getIsSuccess()){
			updateAlert(response);
		}
	}
	
	/**
	 * Call SMSSender to send message to patient
	 * @param message
	 */
	public void sendSMS(String pnumber, String message){
		Logger logger = Logger.getLogger(this.getClass());
		String username="foo";			
		String password="bar";
		SMSSender sender=new SMSSender();
		if(!(sender.login(username,password))){
				logger.error("IMPORTANT:Cannot loggin to Send SMS for username:"+username+" password:"+password);
				return;
		}
		//TODO: Pass patient preferred language. This argument is not presently used.
		SMSResponse response=sender.sendSMSThroughGateway(message, pnumber, senderID, "english");
		if(response.getIsSuccess()){
			//updateAlert(response);
		}
	}
	
	/**
	 * Update Alert based on the response send by 
	 * @param SMSResponse response send by SMS GateWay
	 */
	private void updateAlert(SMSResponse response){
		Logger logger = Logger.getLogger(this.getClass());
		try{
		  Session session = HibernateUtil.getSessionFactory().openSession();
		  session.beginTransaction();
		  String queryString = "update Alert a set a.isExecuted=:isExecuted,a.lastTry=:time,a.serviceInfo=:serviceInfo where aid=:aid and msgId=:msgId and alertType=:alertType";
		  Query query = session.createQuery(queryString);
		  query.setBoolean("isExecuted", true);
		  query.setTimestamp("time", new Date());
		  query.setInteger("msgId", patient.getMsgId());
		  query.setString("aid",patient.getAlertId());
		  query.setString("serviceInfo",response.getTransID() );
		  query.setInteger("alertType", SMS_TYPE);
		  query.executeUpdate();
		  session.getTransaction().commit();
		  session.close();
		}
		catch(Exception ex){
			
			logger.error("Couldnot update Alert for aid:"+patient.getAlertId());
			logger.error("\n ERROR Caused by\n",ex);
		}
	}
	
	
	 
	
}
