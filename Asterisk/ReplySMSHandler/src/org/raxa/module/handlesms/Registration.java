package org.raxa.module.handlesms;

import org.raxa.database.VariableSetter;
import org.raxa.registration.Register;

public class Registration extends Options implements VariableSetter {

	public Registration(String userID, String pnumber) {
		super(userID, pnumber);
	
	}

	@Override
	public String getKeyWord() {
		
		return Keyword.REGISTER.getKeyword();
	}

	@Override
	protected String getDescription() {
		
		return Keyword.REGISTER.getDescription();
	}
	
	@Override
	public String getReply(String message, String pnumber) {
		if(state==-1){												//Redirected
			message=convertMessageFromShortcut(message);
			state=0;
		}
		if(state==0){
			state=state+1;
			return "Choose Your preferLangauge\n"+ getListOfLanguage()+"\n"+getLastLine();
		}
		if(state==1){
			int option=MessageHandler.getMessageOption(message);
			Language[] languages=Language.values();
			if(option>languages.length)										//WE MAY SET delete session to true here or if we want to give user another chance we may continue.
				return RMessage.INVALIDOPTION.getMessage();
			else setLanguage(languages[option-1]);
			
			String reply=new PIDGetter(getUserID(),pnumber,this,false,getLanguage().getLanguage()).getReply(message, pnumber);
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
		
		if(state==2){
			deleteSession=true;
			return ("Hello "+getPname()+" "+handleStateOne(message));
		}
		return null;
	}
	/**
	 * Register the patient to the alert service and return message based on whether the registration is Successful or not
	 * @param message
	 * @return
	 */
	private String handleStateOne(String message) {
		boolean isSuccess=new Register().addReminder(pid, getLanguage().getLanguage(), SMS_TYPE);
			return statusString(isSuccess);
	}
	/**
	 * 
	 * @param status whether the registration is successful or not
	 * @return SMS to be sent
	 */
	private String statusString(boolean status){
		if(status)
			return "You Have Successfully Registered for SMS Service";
		else
			return "Either you are already Registered or Not exist in the system.\n Please consult the Hospital Authority if you are not registered";
			
	}
	
	/**
	 * Its kind of disguise.it will convert the message from the patient in a way that the application will think
	 * that the patient has directly enquired using the code instead of going through menu
	 * 
	 * <p>For ex:Patient query:GET MENU  XYZ 2 will get converted to GET REG
	 * @param message
	 * @return converted message
	 */
	private String convertMessageFromShortcut(String message) {
		return(IncomingSMS.hello+" "+getKeyWord());
	}
	/**
	 * 
	 * @return list of supported langauges
	 */
	private String getListOfLanguage(){
		String list="";int count=1;
		for(Language l:Language.values()){
			list+=count+" ."+l.getLanguage()+"\n";
			++count;
		}
		
		return list;
	}

}
