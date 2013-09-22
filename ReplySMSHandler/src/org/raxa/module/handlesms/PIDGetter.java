package org.raxa.module.handlesms;
import java.util.List;
import org.raxa.database.Patient;


/**
 * Call getReply of this class whenever the other class 'caller' needs to get pid from a phonenumber.
 * <p>Be carefull while implementing this class.See Reminder for example.
 * @author atul
 *
 */

public class PIDGetter extends Options {

	private Options caller;
	private List<Patient> patients;
	private boolean isRegistered;
	private String preferLanguage;
	private String parentMessage;
	
	/**
	 * 
	 * @param userID
	 * @param pnumber
	 * @param helpmeToGetID caller class who needs patient ID
	 * @param isPatientRegistered if the patient is registered for the alert
	 * @param preferLanguage prefer language of the patient
	 */
	public PIDGetter(String userID, String pnumber,Options helpmeToGetID,boolean isPatientRegistered,String preferLanguage) {
		super(userID, pnumber);
		caller=helpmeToGetID;
		this.preferLanguage=preferLanguage;
		isRegistered=isPatientRegistered;
		setPreferLanguage();
	}
	/**
	 * Set patient prefer language if present in Incomingsms.languageMap
	 */
	private void setPreferLanguage(){
		if(preferLanguage!=null &&IncomingSMS.languageMap.containsKey(preferLanguage.toUpperCase()))
			language=IncomingSMS.languageMap.get(preferLanguage.toUpperCase());
	}
	
	@Override
	public String getKeyWord() {
		return null;
	}

	@Override
	public String getDescription() {
		return null;
	}
	
	
	/**
	 * <p>For the caller class
	 * <p>If reply!=null & pid=null & deletesession=true then something wrong with patient number.Caller state=0.Default Langauge in caller object.
	 * Not exist in the system or not registered yet.
	 * <p>If reply=null & pid!=null then there is only one patient with the number.Caller state  increment by 1.Prefer Language is set if 
	 * available in caller object.
	 * <p>If  reply!=null & deletesession=false & pid=null; that mean more than one patient exist.State of PIDGetter changes to 1 and caller needs
	 *to simply send the reply return by PIDGetter.Caller gets updated to PIDGetter in the cache.
	 *<p>PID receive the patient next message.
	 *<p>Once pid is determine the userID again get redirected to caller to do the further step when pid is known.
	 *
	 */
	@Override
	public String getReply(String message, String pnumber) {
		if(state==0 && isRegistered)
			patients=new DatabaseService().getAllRegisteredPatientWithNumber(this.getPhoneNumber());
		else if(state==0 && !isRegistered)
			patients=new DatabaseService().getAllUnRegisteredPatientWithNumber(this.getPhoneNumber(),preferLanguage);
		if(state==0){								//redirected from an Option class who needs PID
			parentMessage=message;
			if(patients==null || patients.size()<=0){
				caller.deleteSession=true;
				return RMessage.NUMBERNOTRECOGNISED.getMessage();
			}
			else if(patients.size()==1){
				caller.deleteSession=false;
				addFeatures(caller,patients.get(0));
				caller.setState(caller.getState()+1);
				return null;
			}
			else if(patients.size()>=1){
				state=1;
				IncomingSMS.redirect(caller, this);
				return getReturnString();
			}
			else{
				caller.deleteSession=true;
				return  RMessage.NUMBERNOTRECOGNISED.getMessage();
			}
		}
		else if(state==1){
			
			int option=MessageHandler.getMessageOption(message);
			if(option==0|| option>patients.size()){		// i.e invalid option by sender
				return RMessage.INVALIDOPTION.getMessage();
			}
			else{
				addFeatures(caller,patients.get(option-1));
				caller.setState(caller.getState()+1);
				IncomingSMS.redirect(this,caller);
				return caller.getReply(parentMessage, pnumber);
			}
		}
		else return null;
	}
	
	/**
	 * Add patient information from Patietn to caller Object
	 * <p>set prefer Langauge
	 * <p>set patient ID
	 * <p>set patient name
	 * @param caller:object that call this class
	 * @param patietn:patient information
	 */
	private void addFeatures(Options caller,Patient p){
		
		
		if(preferLanguage!=null){
			if(IncomingSMS.languageMap.containsKey(preferLanguage.toUpperCase()))
				caller.setLanguage(IncomingSMS.languageMap.get(preferLanguage.toUpperCase()));
			    this.setLanguage(IncomingSMS.languageMap.get(preferLanguage.toUpperCase()));
		}
			
		else if(IncomingSMS.languageMap.containsKey(p.getPatientPreferredLanguage().toUpperCase())){
				caller.setLanguage(IncomingSMS.languageMap.get(p.getPatientPreferredLanguage().toUpperCase()));
				this.setLanguage(IncomingSMS.languageMap.get(p.getPatientPreferredLanguage().toUpperCase()));
				
		}
		
		caller.setPid(p.getPatientId());	
		
		caller.setPname(p.getPatientName());
	}
	
	/**
	 * 
	 * @return a string to be sent to the patient to get patient identity from a list of identity.
	 */
	private String getReturnString(){
		String toReturn="";int count=1;
		for(Patient p:patients){
			toReturn+=count+" "+"for "+p.getPatientName()+"\n";
			++count;
		}
		toReturn+=getLastLine();
		return toReturn;
	}

}
