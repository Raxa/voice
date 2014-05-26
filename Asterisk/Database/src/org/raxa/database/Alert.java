package org.raxa.database;

import java.sql.Timestamp;
/**
 * This class keeps record of all the alerts sent to the patient as well as alerts that need to be sent to the patient today.
 * <p>(alertId,alertType) is expected to be unique.
 * @author atul
 *
 */
public class Alert {
	 private long id;	
	 /**
	  * Alert ID fetch from the RaxaAlert
	  */
     private String aid;
     
     /**
      * Patient UUID for which the alertID is associated
      */
     private String pid;
     /**
      * Alert Type either ivr or sms
      */
     private int  alertType;
     
     /**
      * Message ID for the alert
      */
     private int msgId;
     /**
      * Time to give alert
      */
     private Timestamp scheduleTime;   
     /**
      * Time when the system last tried to give the alert to the patient
      */
     private Timestamp lastTry;
     /**
      * Whether the alert to the patient is successfull
      * <p>Unsuccessful when patietn do not pick up the call or when sms gateway is not able to send the sms
      */
     private boolean isExecuted;
     /**
      * Number of tries by the system to send a successful alert to the paitent.
      */
     private int retryCount;
     /**
      * Service used to give the alert to the patient
      * <p>eg.Transaction ID in case of SMS or phone number in case of call
      */
     private String serviceInfo;
     
     public Alert(String aid,String pid,int alertType,int msgId,Timestamp scheduleTime,Timestamp lastTry){
    	 this.aid=aid;
    	 this.pid=pid;
    	 this.alertType=alertType;
    	 this.msgId=msgId;
    	 this.scheduleTime=scheduleTime;
    	 this.lastTry=lastTry;
    }
     
     public Alert(){}
     
     public long getId(){
    	 return id;
     }
     
     public void setAlertId(long i){
    	 id=i;
     }
     
     public String getAlertId(){
    	 return aid;
     }
     
     public void setAlertId(String id){
    	 aid=id;
     }
     
     public String getPatientId(){
 		return pid;
 	 }
 	
     public void setPatientId(String id){
 		pid=id;
 	 }
     
     public void setAlertType(int type){
    	 alertType=type;
     }
     
     public int getAlertType(){
    	 return alertType;
     }
     
     public void setMessageId(int id){
    	 msgId=id;
     }
     
     public int getMessageId(){
    	 return msgId;
     }
     
    
     public void setScheduleTime(Timestamp time){
    	 scheduleTime=time;
     }
     
     public Timestamp getScheduleTime(){
    	 return scheduleTime;
     }
     
     public Timestamp getLastTried(){
    	 return lastTry;
     }
     
     public void setLastTried(Timestamp datetime){
    	 lastTry=datetime;
     }
     
     public boolean  getIsExecuted(){
    	 return isExecuted;
     }
     
     public void setIsExecuted(boolean  status){
    	 isExecuted=status;
     }
     
     public void setretryCount(int count){
    	 retryCount=count;
     }
     
     public int getretryCount(){
    	 return retryCount;
     }
     
     public String getServiceInfo(){
  		return serviceInfo;
  	 }
     
     public void setServiceInfo(String s){
    	 serviceInfo=s;
     }
     
}
