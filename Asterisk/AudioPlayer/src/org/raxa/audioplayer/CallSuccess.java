package org.raxa.audioplayer;


import java.util.Date;
import org.raxa.database.*;
import org.hibernate.Query;
import org.hibernate.Session;
import org.apache.log4j.Logger;
/**
 * Updates the database when a successfull call is made
 * @author atul
 *
 */
public class CallSuccess implements VariableSetter {

	private String aid;
	
	/**
	 * the msg Id that is played
	 */
	private int msgId;
	
	/**
	 * whether the call is executed or not
	 */
	private boolean isExecuted;
	
	/**
	 * service that is 
	 */
	private String serviceInfo;
	
	public CallSuccess(int msgId,String aid,boolean isExecuted,String serviceInfo){
		this.msgId=msgId;
		this.aid=aid;
		this.isExecuted=isExecuted;
		this.serviceInfo=serviceInfo;
	}
	
	/**
	 * update Alert about the status of the call.Wether successful or not.
	 */
	public void updateAlert(int alertType){
		 Logger logger = Logger.getLogger(this.getClass());
		try{
		  logger.info("Updating Alert for alertId:"+aid);
		  Session session = HibernateUtil.getSessionFactory().openSession();
		  session.beginTransaction();
		  String queryString = "update Alert a set a.isExecuted=:isExecuted,a.lastTry=:time,a.serviceInfo=:serviceInfo where aid=:aid and msgId=:msgId and alertType=:alertType";
		  Query query = session.createQuery(queryString);
		  query.setBoolean("isExecuted", isExecuted);
		  query.setTimestamp("time", new Date());
		  query.setInteger("msgId", msgId);
		  query.setString("aid",aid);
		  query.setString("serviceInfo", serviceInfo);
		  query.setInteger("alertType", alertType);
		  query.executeUpdate();
		  session.getTransaction().commit();
		  session.close();
		}
		catch(Exception ex){
			
			logger.error("Couldnot update Alert");
			logger.error("\n ERROR Caused by\n",ex);
		}
	}
	
}