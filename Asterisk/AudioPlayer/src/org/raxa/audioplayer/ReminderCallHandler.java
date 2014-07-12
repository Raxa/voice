package org.raxa.audioplayer;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.File;
import java.net.URL;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.asteriskjava.fastagi.AgiChannel;
import org.asteriskjava.fastagi.AgiException;
import org.asteriskjava.fastagi.AgiRequest;
import org.asteriskjava.fastagi.BaseAgiScript;
import org.asteriskjava.fastagi.command.RecordFileCommand;
import org.asteriskjava.manager.action.CommandAction;
import org.asteriskjava.manager.response.CommandResponse;
import org.asteriskjava.manager.AuthenticationFailedException;
import org.asteriskjava.manager.ManagerConnection;
import org.asteriskjava.manager.ManagerConnectionFactory;
import org.asteriskjava.manager.TimeoutException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.raxa.alertmessage.MessageInterface;
import org.raxa.registration.Register;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.raxa.database.HibernateUtil;
import org.raxa.database.Patient;
import org.raxa.database.VariableSetter;
import org.raxa.alertmessage.MedicineInformation;
import org.raxa.alertmessage.MessageTemplate;
/**
 * Outgoing Call Context here sets the following channel variable
 * totalItem;item0,item1....,count
 * 
 * CAUTION:Even if the patient has hung up the program is going to execute until it meets an exception or termination.
 * @author atul
 *
 */
public class ReminderCallHandler extends CallHandler
{
	private AgiRequest request;
	AgiChannel channel;
	String pid;
	private Logger logger = Logger.getLogger(this.getClass());
	
	ReminderCallHandler(AgiRequest request, AgiChannel channel, List<Patient> patientList,String language) {
		super(channel,request,language);
		this.request=request;
	    this.channel=channel;
		try{
    		service(request, channel);
			playReminderMenu(patientList,language);
			} catch(Exception ex){
				logger.error("\nCaused by:\n",ex);
			}	}
	

    
	  /**
	 * checks whether the call is incoming or outgoing.Handles the call accordingly
	 */
    public void service(AgiRequest request, AgiChannel channel) throws AgiException{

	}
	
	
	  /**
   * Plays medicine Reminder to the patient
   * @param pid
   * @throws AgiException
   */
  private void medicineReminder(String pid) throws AgiException {
  		Date time=new Date();
  		time.setHours(0);
			time.setMinutes(1);					//set time to today's midnight so that entire medicine prescription of today can be fetched.
			
			List<MedicineInformation> listofinfo=new ReminderExtractor().getMedicineInfo(pid, time);
			String noReminderMsg = getValueFromPropertyFile("noReminders","english");			
			String header1=getValueFromPropertyFile("IncomingCallMedicineInfoHeader1","english");
			String header2=getValueFromPropertyFile("IncomingCallMedicineInfoHeader2","english");
			String ttsNotation= getTTSNotation(language);
			if(listofinfo == null || listofinfo.size() == 0)
			{
			playUsingTTS(noReminderMsg,ttsNotation);
			return;
			}
			if(header1!=null)
				playUsingTTS(header1,ttsNotation);
			if(header2!=null)
				playUsingTTS(header2,ttsNotation);
			for(MedicineInformation info : listofinfo )
				playUsingTTS(new MessageTemplate().getTextToconvertToVoice(info),ttsNotation);
			
	}
  
  /**
   * Takes the Keyword and all the possible patientList associated with the phone number and patient prefer language
   * Checks if the keyword require pid?
   * If yes,ask for the patient identity among patientList.
   * Stores the pid and fetch asked information using the pid
   * 
   * @param keyWord
   * @param patientList
   * @param language
   * @throws AgiException
   * @throws Exception
   */
  public void playReminderMenu(List<Patient> patientList,String language) throws AgiException,Exception{
  	
  	String reminderMenuVoiceFileLocation=getValueFromPropertyFile(language.toLowerCase(),"mainmenu");
  	String reminderMenuText=getValueFromPropertyFile("reminderMenuText","english");
  	
  	channel.setVariable("reminderMenuPlayCount", String.valueOf((Integer.parseInt(channel.getVariable("reminderMenuPlayCount")))+1));
  	
  					//char option=getPatientOption(mainmenuVoiceFileLocation,"11000","1234569",2);
  		char option= getOptionUsingTTS(reminderMenuText,"1234569","5000",2);
  	String keyWord=analyseOption("remindermenu",option);
  	if(keyWord==null){
  		channel.hangup();
  		logger.error("Unable to get what patient opted in reminder menu of language "+language);
  		return;
  	}
  	
  	boolean isPatientRegistered=true;boolean isAllowed=true;
  	int count=Integer.parseInt(channel.getVariable("reminderMenuPlayCount"));
  	
  	if(patientList==null || patientList.size()==0)
  		isPatientRegistered=false;
  	//All option that needs to check if the patient is in the alert System already should add that option to the or Statement
  	//This is to deal with fact that if a patient is not register and he keeps on opting for the option that needs patient to be registered
  	if(keyWord.toLowerCase().equals("reminder")||keyWord.toLowerCase().equals("deregister")){
  		if(!isPatientRegistered && count<=2){						//Play main menu only twice 
  			playUsingTTS(getValueFromPropertyFile("notRegistered","english"),getTTSNotation(language));
  			playReminderMenu(patientList, language);
  			return;
  		}
  		if(!isPatientRegistered){
  			channel.hangup();
  			logger.error("patient unable to choose a valid reminder menu option");
  			return;
  		}
  	}
  	
  	
  	if(keyWord.toLowerCase().equals("register")){
  		
  		Register r=new Register();
  		List<Patient> p=r.getpatientFromNumber(pnumber,language);
  		pid=getPid(p);
  		if(pid==null)
  			playUsingTTS(getValueFromPropertyFile("PatientNotExist", "english"),getTTSNotation(language));
  		else{
  			if(r.addReminder(pid, language, IVR_TYPE))
  				playUsingTTS(getValueFromPropertyFile("successfulRegister", "english"),getTTSNotation(language));
  			else
  				playUsingTTS(getValueFromPropertyFile("unsuccessfulRegister", "english"),getTTSNotation(language));
  		}
  	}
  	
  	else if(keyWord.toLowerCase().equals("reminder")){
  		pid=getPid(patientList);
  		if(pid==null){
  			playUsingTTS(getValueFromPropertyFile("PatientNotExist", "english"),getTTSNotation(language));
  			return;
  		}
  		//There may be a chance that language currently playing is not the preferred language as specified in database.if we want to change 
  		//language to preferLanguage below code should be included.otherwise it will continue with the current language.
  /*   		for(Patient p:patientList){
  			if(p.getPatientId().equals(pid))
  				language=p.getPatientPreferredLanguage();
  		}
  		
  */ 		else
  			medicineReminder(pid);
  		
  	}
  	
  	else if (keyWord.toLowerCase().equals("deregister")){
  			if(new Register().deleteReminder(pid,IVR_TYPE))
  					playUsingTTS(getValueFromPropertyFile("successUnregister", "english"),getTTSNotation(language));
  			else 
  					playUsingTTS(getValueFromPropertyFile("failUnregister", "english"),getTTSNotation(language));
  	}
  	else if(keyWord.toLowerCase().equals("mainmenu")){
  		return;
  	}
  }

 
    /**
     * Given a list of possible patient it returns the pid.
     * then it stores the pid as the channel variable to avoid calculation again and agai
     * 
     * Disadvantage:once patient pid is set it cannot query for anyone else while in the call.
     * @param patientList
     * @return
     * @throws AgiException
     */
    public String getPid(List<Patient> patientList) throws AgiException{
    	String pid;
    	pid=channel.getVariable("pid");
    	if(pid==null){
    		pid=getPatientIdfromList(patientList);
    		logger.info("Patient with pnumber:"+pnumber+"chose pid:"+pid);
    	}
		channel.setVariable("pid", pid);
		return pid;
    }
    

 
    /**
     * This speaks out name of all the patient name one by one and return the uuid for the name the caller chose
     * 
     * What I don't like about it:after calling every name it will produce a beep and ask for option.If not get an answer say the other patient name and
     * again beep,wait ask for an option
     * 
     * can be changed to any other file.
     * 
     *  Use in Incoming-Call Context
     * @throws AgiException 
     * 
     */
    
    public String getPatientIdfromList(List<Patient> patientList) throws AgiException{
    	int numberOfTries=2;
    	
    	if(patientList==null)
    		return null;
    	if(patientList.size()==1)
    		return patientList.get(0).getPatientId();
    	
    	String message1=getValueFromPropertyFile("patientNameMessage1","english");
    	String message2=getValueFromPropertyFile("patientNameMessage2","english");
    	ttsNotation = getTTSNotation(language);
    	
    	int count=0;char option='0';
    	String space=" ";String message="";
    	
    	
    	
	    	for(Patient patient:patientList){
	    		
	    		message+=message1+space+patient.getPatientName()+space+message2+space+(++count)+space;
	    	
	    	}
	    	
	    	option= getOptionUsingTTS(message,"123456","5000",2);
	    	
	    	int opttionNumericValue=option-'0';
	    	
	    	
	    	
	    	
	    	
	    	    	
	    	if(opttionNumericValue>=1 && opttionNumericValue<=9 && opttionNumericValue<=patientList.size())
	    		return patientList.get(opttionNumericValue-1).getPatientId();
	    	
	    	
	    	else{
	    		String toSay="Sorry You pressed an invalid input";
				playUsingTTS(toSay,ttsNotation);
	    	}
	    		
	    	
    	
    	
       		channel.hangup();
    		logger.error("Patient didnot enter a valid input");
    		return null;
    	
    }
    
}