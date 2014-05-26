package org.raxa.module.handlesms;

import org.raxa.database.VariableSetter;
import org.raxa.registration.Register;
/**
 * Unregister patient from the Service
 * <p>Keyword:UNREG
 * @author Atul Agrawal
 *
 */
public class UnRegister extends Options implements VariableSetter  {

	public UnRegister(String userID, String pnumber) {
		super(userID, pnumber);
	}

	@Override
	public String getKeyWord() {
		
		return Keyword.UNREGISTER.getKeyword();
	}

	@Override
	protected String getDescription() {
		
		return Keyword.UNREGISTER.getDescription();
	}

	@Override
	public String getReply(String message, String pnumber) {
		if(state==-1){
			message=convertMessageFromShortcut(message);
			state=0;
		}
		
		if(state==0){
			String reply=new PIDGetter(getUserID(),pnumber,this,true,getLanguage().getLanguage()).getReply(message, pnumber);
			if(deleteSession==true)
				return RMessage.NUMBERNOTRECOGNISED.getMessage();
			else if(pid!=null && reply==null)
				return ("Hello "+getPname()+" "+handleStateOne(message));								//PID Found as only one patient with that number exist.
			else if(reply==null){
				deleteSession=true;
				return RMessage.NOTHINGTOREPLY.getMessage();
			}
			else
				return reply;
		}
		
		if(state==1){
			deleteSession=true;
			return ("Hello "+getPname()+" "+handleStateOne(message));
		}
		return null;
	}
		
		
	
	/**
	 * PID is known.Now this function tries to unregister the patient from the sms Alert
	 * @param message
	 * @return whether successfully registerd or not
	 */
	private String handleStateOne(String message) {
		boolean isSuccess=new Register().deleteReminder(getPid(), SMS_TYPE);
		return getStatus(isSuccess);
	}
	/**
	 * 
	 * @param isSuccess
	 * @return  whether successfully registerd or not
	 */
	private String getStatus(boolean isSuccess) {
		if(isSuccess)
			return "You Have Successfully Unregistered from the Service";
		else
			return "Sorry something went wrong.Please contact the hospital Authority";
	}
	/**
	 * Convert message to shortuct "GET UNREG"
	 * @param message
	 * @return
	 */
	private String convertMessageFromShortcut(String message) {
		return(IncomingSMS.hello+" "+getKeyWord());
	}
	
}
