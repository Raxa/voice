package org.raxa.module.handlesms;

import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.raxa.database.Patient;
import org.raxa.database.VariableSetter;

/**
 * state=-1 suggest it is redirected from Reminder.
 * state=0 suggest it has been asked directly.
 * @author atul
 *
 */
public class Reminder extends Options implements VariableSetter {
	
	
	public enum RemKey{
		NOW("NOW","Get Which Medicine To take Now.Short:GET REM NOW"),
		TODAY("TODAY","Get Which Medicine To take today.Short:GET REM TODAY"),
		NOWON("NOWON","Get Which Medicine to take afterwards.Short:GET REM NOWON");
		
		private String key;
		private String description;
		RemKey(String a,String b){
			key=a;
			description=b;
		}
		
		public String getDescription(){
			return description;
		}
		
		public String getKey(){
			return key;
		}
	}
	
	public static Set<String> keywords;
	public static String defaultString;
	public static int offsetHours=0;
	public static int offsetMinutes=-30;
	private boolean isRedirected=false;
	
	public Reminder(String userID, String pnumber) {
		super(userID, pnumber);
		setNumberVerification(true);
		initialiseKeyword();
	}
	
	private static void initialiseKeyword(){
		if(Reminder.keywords==null){
			Reminder.keywords=new HashSet<String>();
			for(RemKey r:RemKey.values())
				Reminder.keywords.add(r.getKey());
		}
		if(Reminder.defaultString==null)
			Reminder.defaultString=RemKey.TODAY.getKey();
	}


	@Override
	public String getKeyWord() {
		
		return Keyword.REMINDER.getKeyword();
	}

	@Override
	public String getDescription() {
		
		return Keyword.REMINDER.getDescription();
	}
	/**
	 * <p>state 0 object doesnot have PID
	 * <p>state 1 object has pid;
	 *
	 * For the caller class
	 * <p>If reply!=null & pid=null & deletesession=true then something wrong with patient number.Caller state=0.Default Langauge in caller object.
	 * Not exist in the system or not registered yet.
	 * <p>If reply=null & pid!=null then there is only one patient with the number.Caller state  increment by 1.Prefer Language is set if 
	 * available in caller object.
	 * <p>If  reply!=null & deletesession=false & pid=null; that mean more than one patient exist.State of PIDGetter changes to 1 and caller needs
	 *to simply send the reply return by PIDGetter.Caller gets updated to PIDGetter in the cache.
	 *<p>PID receive the patient next message.
	 *<p>Once pid is determine the userID again get redirected to caller to do the further step when pid is known.
	 */
	@Override
	public String getReply(String message, String pnumber) {
		
		if(state==-1){						//Redirected from Main Menu
			state=0;
			isRedirected=true;
			return getMenuDescription();
		}
		
		if(state==0){
			if(isRedirected)
				message=convertMessageFromShortcut(message);
			if(isRedirected && message==null){
				deleteSession=true;
				return RMessage.INVALIDOPTION.getMessage();
			}
			String reply=new PIDGetter(getUserID(),pnumber,this,true,null).getReply(message, pnumber);
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
		
		
		else if(state==1){
			deleteSession=true;
			return ("Hello "+getPname()+" "+handleStateOne(message));								//state=1. Hence pid is already known.
		}
		
		
		return RMessage.NOTHINGTOREPLY.getMessage();
	}
	

	/**
	 * Convert user chose option to the shortcute code
	 * <p>eg. convert "XYZ 2" to "GET REM TODAY
	 * 
	 * @param message
	 * @return
	 */
	private String convertMessageFromShortcut(String message) {
		int option=MessageHandler.getMessageOption(message);
		if(option==0)
			return null;
		RemKey[] keys=RemKey.values();
		if(!(option<=keys.length))
			return null;
		else return(IncomingSMS.hello+" "+getKeyWord()+" "+keys[option-1].getKey().toUpperCase());
			
		
	}
	/**
	 * Once the patient pid is known it offset the current Date(new Date()) by desired amount to satisfy patietn query(based on keyword)
	 * <p>It then calls DatabaseService.getReminder to get patient Information
	 * @param message
	 * @return String containing patient medical Information
	 */
	private String handleStateOne(String message) {
		String keyword=verifyMessageSyntax(message);
		if(keyword==null){
			deleteSession=true;
			return getMenuMessage();
		}
		
		Date today=new Date();Date startTime=null;Date endTime=null;
		if(keyword.equals(Reminder.defaultString)){
			startTime=null;endTime=null;
		}
		else if(keyword.equals(RemKey.TODAY.getKey())){
			startTime=null;endTime=null;
		}
		else if(keyword.equals(RemKey.NOW.getKey())){
			endTime=offsetTime(new Date(),-1*Reminder.offsetHours,-1*Reminder.offsetMinutes);
			startTime=offsetTime(new Date(),Reminder.offsetHours,Reminder.offsetMinutes);
		}
		else if(keyword.equals(RemKey.NOWON.getKey())){
			startTime=offsetTime(new Date(),Reminder.offsetHours,Reminder.offsetMinutes);
			endTime=null;
		}
		
		String reply=new DatabaseService().getReminder(pid, today, startTime, endTime, SMS_TYPE);
		if(reply==null){
			deleteSession=true;
			return RMessage.NOTHINGTOREPLY.getMessage();
		}
		else
			return reply;
	}
	/**
	 * Verify if the message is of desired syntax
	 * @param message
	 * @return
	 */
	private String verifyMessageSyntax(String message){
		String[] split=message.toUpperCase().split(" ");
		if(split.length==2)
			return Reminder.defaultString;
		else if(split.length>3)
			return null;
		else{
			if(Reminder.keywords.contains(split[2]))
				return split[2];
			else return null;
		}
	}
	
	/**
	 * USING the fact that enum returns element in the way they are declared.
	 * @return
	 */
	private String getMenuDescription(){
		int count=1;String message="Type "+getUserID()+" your option \n";
		for(RemKey r:RemKey.values()){
			message=message+count+". "+r.getDescription()+"\n";
			count++;
		}
		return message;
	}
	/**
	 * offset date by hours and minutes
	 * @param currentDate
	 * @param hours  offset hours
	 * @param minutes offset minute
	 * @return offset date
	 */
	private static Date offsetTime(Date currentDate,int hours,int minutes){
		Calendar cal = Calendar.getInstance();
		cal.setTime(currentDate);
		cal.add(Calendar.HOUR, hours);
		cal.add(Calendar.MINUTE,minutes );
		Date newDate = cal.getTime();
		return newDate;
	}
	
	private String getMenuMessage(){
		return null;
	}
}
