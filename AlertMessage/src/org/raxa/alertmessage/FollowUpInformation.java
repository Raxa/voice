package org.raxa.alertmessage;

import java.sql.Timestamp;
/**
 * Contains information that will be used while giving an alert to the patient.
 * 
 * @author Atul Agrawal
 *
 */
public class FollowUpInformation {
	private String name;
	private String dose;
	private String type;
	private int mode;
	private Timestamp scheduleTime;
	private String response;
	public FollowUpInformation(){}
	
	public FollowUpInformation(String name,String dose,String type,int mode,Timestamp scheduleTime){
		this.name=name;
		this.dose=dose;
		this.mode=mode;
		this.scheduleTime=scheduleTime;
		this.type=type;
	}
	
	public FollowUpInformation(String name,String dose,String type,int mode){
		this(name,dose,type,mode,null);
	}
	
    public void setScheduleTime(Timestamp time){
   	 scheduleTime=time;
    }
    
    public Timestamp getScheduleTime(){
   	 return scheduleTime;
    }
    
    public String getName(){
    	return name;
    }
    
    public String getType(){
    	return type;
    }
    
    public String getDose(){
    	return dose;
    }
    
    public int getMode(){
    	return mode;
    }
    
    public int getResponse(){
    	return mode;
    }
    
    public int setMode(){
    	return mode;
    }
    
    
    public void setName(String s){
    	name=s;
    }
    
    public void setDose(String s){
    	dose=s;
    }
    
    public void setMode(int s){
    	mode=s;
    }
    

    public void setType(String s){
    	type=s;
    }
    
    public void setResponse(String s){
    	response=s;
    }
}
