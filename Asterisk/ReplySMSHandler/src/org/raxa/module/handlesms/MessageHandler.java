package org.raxa.module.handlesms;
import java.util.Date;
import org.raxa.module.sms.SMSResponse;
import org.raxa.module.sms.SMSSender;

public class MessageHandler {
	
	String message;
	
	/**
	 * Return option from a message
	 * <p>eg. if message is XYZ 1 it return 1
	 * <p> return 0 if error occurs or invalid option
	 * @param message
	 * @return patient option
	 */
	public static int getMessageOption(String message) {
		String[] split=message.split(" ");
		int option=0;
		try{
			option=Integer.parseInt(split[1]);
		}
		catch(Exception ex){}
		return option;
	}
	
	public MessageHandler(String message){
		this.message=message;
	}
	
	public MessageHandler(){}
	
	public IMessage getMessageFormat(){
		return getMessageFormat(message);
	}
	
	/**
	 * Parse an message and determing its type.
	 * <p> Type will be one of the type described in IMessage enum.
	 * @param message
	 * @return
	 */
	public IMessage getMessageFormat(String message){
		if(message==null)
			return IMessage.NULL;
		String[] split=message.split(" ");
		if(!(split.length>=2))
			return IMessage.WRONGFORMAT;
		String firstWord=split[0].toUpperCase();
		if(firstWord.equals(IncomingSMS.hello))
			return IMessage.GET;
		if((firstWord.length()==3 && isAlpha(firstWord)))
			return IMessage.ID;
		return IMessage.UNKNOWN;
	}
	
	/**
	 * checks if the firstword is a set of alphabets.
	 * @param firstword
	 * @return isAlphas?
	 */
	public boolean isAlpha(String firstword) {
	    return firstword.matches("[a-zA-Z]+");
	}
	
	
	//Langauge may come as null.Take care
	/**
	 * Sends an SMS and call function to update the database.
	 * <p>Sets senderID.IMORTANT:No message wil be sent if its incorrect. </p>
	 * @param message
	 * @param inDate
	 * @param pnumber
	 * @param reply
	 * @param language
	 */
	public void sendSMS(String message,Date inDate,String pnumber,String reply, Language language) {
		String senderId="TEST SMS"; Date outDate=new Date();
	//	IncomingSMS.out.println("Sending SMS:"+reply+"\n \nReceiver PhoneNumber:"+pnumber+" in Language:"+language.getLanguage());
		SMSSender a=new SMSSender();
		a.login("user","pass");
		if(language==null)
			language=IncomingSMS.defaultLanguage;
		SMSResponse response=a.sendSMSThroughGateway(reply, pnumber, senderId,language.getLanguage());
		updateDatabase(inDate,outDate,pnumber,message,reply.replace("\n", " "),response.getIsSuccess(),response.getTransID());  // TO BE IMPLEMENTED
	}
	/**
	 * Call Database service to update Database to store smsRecord
	 */
	private void updateDatabase(Date inDate, Date outDate, String pnumber,String message,
			String reply, boolean isSuccess, String transID) {
		DatabaseService.updateSMSResponse(pnumber,inDate,message,reply,outDate,isSuccess,transID);
		
	}
	
	/**
	 * 
	 * @param reply message type
	 * @return return a sms to be sent for that message type
	 */
	public String getErrorMessage(RMessage message) {
		
		return message.getMessage();
	}
	
	/**
	 * 
	 * @return an error message
	 */
	public String getErrorMessage(){
		return "Sorry Invalid Input.Try Again.Type GET MENU to get menu option";
	}
	
	/**
	 * 
	 * @param Incoming message type
	 * @return 
	 */
	public String getErrorMessage(IMessage messageType) {
		
		return messageType.getDescription();
	}
	
	
}
