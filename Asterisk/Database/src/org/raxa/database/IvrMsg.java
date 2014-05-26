package org.raxa.database;
/**
 * This class stores the message in a particular format.
 * <p>Maps msgId in alert table to ivrId here.
 * <p>eg.Type example is: 
 * <h3>1 23 1 {hello Atul}</h3>
 * <h3>2 23 2 {This is your medicine Reminder}</h3>
 * <h3>3 23 3 {Take 2 tablets of acetamide}</h3>
 * <h3>4 24 1 {hello Apurv}</h3>
 * 
 * @author Atul Agrawal
 *
 */
public class IvrMsg {
	/**
	 * AUTO INCREMENT by mySQL	
	 */
	private int id;
	/**
	 * msg ID for the alert
	 */
	private int ivrId;
	/**
	 * different type of format in message maps to different itemNumber
	 */
	private int itemNumber;
	/**
	 * Message content for the alert
	 */
	private String content;
	
	public IvrMsg(int id,int number,String content){
		
		this.ivrId=id;
		this.itemNumber=number;
		this.content=content;
	}
	
	public IvrMsg(){}
	
	public int getId(){
		return id;
	}
	
	public void setId(int id){
		this.id=id;
	}
	
	public int getIvrId(){
		return ivrId;
	}
	
	public void setIvrId(int id){
		ivrId=id;
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
