package org.raxa.module.handlesms;
/**
 * Type of Message that will be received from user query
 * @author atul
 *
 */
public enum IMessage {
	NULL(false,0,"Sorry you have sent a blank message.Don't do that Again"),      //Blank Message
	GET(true,3,""),		//Message containing GET as the first WORD
	WRONGFORMAT(false,1,"Sorry your message was of wrong format."),  //Message in an unexpected format
	UNKNOWN(false,2,"We don't have any clue what you are trying to do.Check out GET MENU"),		
	ID(true,4,"");			//Message where first word is an ID
	
	
	private boolean toContinue;
	private int status;
	private String description;
	
	IMessage(boolean toContinue,int status,String description){
		this.toContinue=toContinue;
		this.status=status;
		this.description=description;
	}
	
	public boolean getToContinue(){
		return toContinue;
	}
	
	public int getStatus(){
		return status;
	}
	
	public String getDescription(){
		return description;
	}
	
	
}
