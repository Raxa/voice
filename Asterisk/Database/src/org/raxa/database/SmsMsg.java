package org.raxa.database;

public class SmsMsg {
	 /**This class stores the message in a particular format.
	 * <p>Maps msgId in alert table to ivrId here.
	 * <p>eg.Type example is: 
	 * <h3>1 23 1 {hello Atul}</h3>
	 * <h3>2 23 2 {This is your medicine Reminder}</h3>
	 * <h3>3 23 3 {Take 2 tablets of acetamide}</h3>
	 * <h3>4 24 1 {hello Apurv}</h3>
	 */
	/**
	 * AUTO INCREMENT 
	 */
	private int id;	
	/**
	 * msgId for the aler	t
	 */
	private int smsId;
	/**
	 * different type of format in message maps to different itemNumber
	 */
	private int itemNumber;
	/**
	 * Message content for the alert
	 */
	private String content;

public SmsMsg(int id,int number,String content){
	
	this.smsId=id;
	this.itemNumber=number;
	this.content=content;
}

public SmsMsg(){}

public int getId(){
	return id;
}

public void setId(int id){
	this.id=id;
}

public int getSmsId(){
	return smsId;
}

public void setSmsId(int id){
	smsId=id;
}

public int getItemNumber(){
	return itemNumber;
}

public void setItemNumber(int number){
	itemNumber=number;
}

public String getContent(){
	return content;
}

public void setContent(String text){
	content=text;
}

}