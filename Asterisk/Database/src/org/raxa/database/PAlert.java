package org.raxa.database;
/**
 * This table keeps record of patient ID and alert It is associated with
 * (pid,alertType) is expected to be unique.
 * @author atul
 *
 */
public class PAlert {
	/**
	 * ID generated by mysql
	 */
	private int paid;
	/**
	 * Patient UUID
	 */
	private String pid;
	/**
	 * Alert for which patient is Registered
	 */
	private int alertType;
	
	public PAlert(String pid,int alertType){
		this.pid=pid;
		this.alertType=alertType;
	}
	
	public PAlert(){}
	
	public int getPatientAlertId(){
		return paid;
	}
	
	public void setPatientAlertId(int id){
		paid=id;
	}
	
	public String getPatientId(){
		return pid;
	}
	
	public int getAlertType(){
		return alertType;
	}
	
	public void setPatientId(String id){
		pid=id;
	}
	
	public void setAlertType(int type){
		alertType=type;
	}
}