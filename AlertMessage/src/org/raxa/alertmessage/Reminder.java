package org.raxa.alertmessage;

import java.util.Date;
import java.util.List;
/**
 * The json received by raxaalert maps to the reminder class
 * 
 * 
 * @author atul
 *
 */
public class Reminder {
	
	/**
	 * alertId of the patient.
	 */
	private String aid;
	/**
	 * message received by making a call to Raxa alert eg.Take x of y
	 */
	private String rawmessage;
	/**
	 * Date in which the reminder is to be set.
	 */
	private Date time;
	/**
	 * contains all the message in Json Format that needs to be reminded to the patient today
	 */
	private List<String> templatizeMessage;
	
	public Reminder(String aid,String rawmessage,Date time){
		this.aid=aid;
		this.time=time;
		this.rawmessage=rawmessage;
	}
 
	public String getAlertId(){
		return aid;
	}
	
	public String getrawmessage(){
		return rawmessage;
	}
	
	public Date getTime(){
		return time;
	}
	
	public void setTemplatizeMessage(List<String> message){
		templatizeMessage=message;
	}
	
	public List<String> getTemplatizeMessage(){
		return templatizeMessage;
	}
	
	public String toString(){
		return "Adding aid:"+aid+" on time:"+time+" with message: "+rawmessage;
	}
	
	
	
}
