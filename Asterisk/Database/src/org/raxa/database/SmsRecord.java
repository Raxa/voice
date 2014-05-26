package org.raxa.database;

import java.sql.Timestamp;
/**
 * This class maintatins a record of all queries done by patient and replies given by the system to that query
 * @author atul
 *
 */
public class SmsRecord {
	/**
	 * Auto Increment
	 */
	private long id;
	/**
	 * Patient phone number
	 */
	private String pnumber;
	/**
	 * Time when the query reaches to SMS Gateway
	 */
	private Timestamp inTime;
	/**
	 * Query by the patient
	 */
	private String msg;
	/**
	 * Reply by the system
	 */
	private String reply;
	/**
	 * Time when system replied
	 */
	private Timestamp outTime;
	/**
	 * wether the reply by the system is successfully sent by the SMS Gateway
	 */
	private boolean isExecuted;
	/**
	 * Transaction ID associated with the service;Given by SMS Gateway
	 */
	private String transactionId;
	
	public SmsRecord(String pnumber,Timestamp inTime,String msg,String reply,Timestamp outTime,boolean isExecuted,String transactionId){
		
		this.pnumber=pnumber;
		this.inTime=inTime;
		this.msg=msg;
		this.reply=reply;
		this.outTime=outTime;
		this.isExecuted=isExecuted;
		this.transactionId=transactionId;
	}
	
	public long getId(){
		return id;
	}
	
	public void setId(long id){
		this.id=id;
	}
	
	public String getPatientPhoneNumber(){
		return pnumber;
	}
	
	public void setPatientPhoneNumber(String number){
		pnumber=number;
	}
	
	public String getIncomingtMessage(){
		return msg;
	}
	
	public void setIncomingMessage(String message){
		msg=message;
	}
	
	public String getReply(){
		return reply;
	}
	
	public void setReply(String s){
		reply=s;
	}
	public String geTransactionId(){
		return transactionId;
	}
	
	public void setTransactionId(String s){
		transactionId=s;
	}
	
	public boolean getIsExecuted(){
		return isExecuted;
	}
	
	public void setIsExecuted(boolean b){
		isExecuted=b;
	}
	
	public Timestamp getInTime(){
		return inTime;
	}
	
	public void setInTime(Timestamp t){
		inTime=t;
	}
	
	public Timestamp getoutTime(){
		return outTime;
	}
	
	public void setOutTime(Timestamp t){
		outTime=t;
	}

}
