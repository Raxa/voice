/*

 */

package org.raxa.audioplayer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import org.apache.log4j.Logger;
import org.asteriskjava.fastagi.AgiChannel;
import org.asteriskjava.fastagi.AgiException;
import org.asteriskjava.fastagi.AgiRequest;
import org.asteriskjava.fastagi.BaseAgiScript;
import org.hibernate.Query;
import org.hibernate.Session;
import org.raxa.alertmessage.ContentFormat;
import org.raxa.alertmessage.MedicineInformation;
import org.raxa.alertmessage.MessageTemplate;
import org.raxa.database.HibernateUtil;
import org.raxa.database.Patient;
import org.raxa.registration.Register;
import org.raxa.alertmessage.MessageInterface;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.raxa.database.VariableSetter;

import src.org.raxa.scheduler.Alert;

/**
 * Outgoing Call Context here sets the following channel variable
 * totalItem;item0,item1....,count
 * 
 * CAUTION:Even if the patient has hung up the program is going to execute until it meets an exception or termination.
 * @author atul
 *
 */
public class followUpCallHandler extends BaseAgiScript implements MessageInterface,VariableSetter
{
	private AgiRequest request;
	private AgiChannel channel;
	private Logger logger = Logger.getLogger(this.getClass());
	String language;
	String pnumber;
	final String speed="1";							//The default speed to play voice file 
	String pid;
	
	/**
	 * checks whether the call is incoming or outgoing.Handles the call accordingly
	 */
    public void service(AgiRequest request, AgiChannel channel) throws AgiException{
    	try{
    		answer();
    		language=null;
    		pnumber=null;
	    	this.request=request;
	    	this.channel=channel;
	    	if(request.getContext().equals("incoming-call"))
	        	handleIncomingCall();
	        if(request.getContext().equals("outgoing-call"))
	        	handleOutgoingCall();

	        return;
    	}
    	catch(AgiException ex){
    		
    		logger.error("IMPORTANT:SOME ERROR WHILE HANDLING THE CALL with caller number:"+pnumber);
    		logger.info("Hanging the call");
    		hangup();
    	}
    	catch(Exception ex1){
    		
    		logger.error("IMPORTANT:SOME ERROR WHILE HANDLING THE CALL with caller:"+pnumber);
    		logger.info("Hanging  the call");
    		hangup();
    	}
    	
    }
    


	private void handleOutgoingCall() {

		String defaultLanguage = null,pnumber=null;
    	pnumber=channel.getName();		//IMPORTANT  DEPEND ON THE TRIE SERVICE WE WILL BE USING
    	//HARDCODE HERE pnumber=SIP/100abc;
    	defaultLanguage=getValueFromPropertyFile("0","languageMap");
    	language=defaultLanguage;
    	
    	Patient followUpPatient = getPatient(channel.getVariable("aid"));
    	String languagePlaying= followUpPatient.getPatientPreferredLanguage();
    	if(languagePlaying==null){
    		logger.error("Unable to get Language when playing to user withn number "+pnumber);
    		return;
    	}
    	language=languagePlaying;
    	channel.setVariable("numberOftimesMenuPlayed", "0");
    	
    	Boolean isPatientAvailable = sayWelcomeAndConfirmPatientIdentity(followUpPatient);
    	if(!isPatientAvailable)
    	{
    		logger.error("Required atient unavailable while calling"+pnumber);
    		return;
    	}
    	
    	//Inform patient to press pound (#) after each input
		String header=getValueFromPropertyFile("followUpEscapeInfo","english");
		String ttsNotation=getTTSNotation(language);
		if(header!=null)
			playUsingTTS(header,ttsNotation);

    	requestFollowUpInfo();
		
	}

	private void requestFollowUpInfo() {
		Date time=new Date();
		//set time to today's midnight so that entire medicine prescription of today can be fetched.
		
		List<FollowUpInformation> listofinfo=new FollowUpExtractor().getFollowUpInfo(pid, time);
		String aid=request.getParameter("aid");
		String ttsNotation=getTTSNotation(language);
		
		while(true){
	    	int readItemSoFar=Integer.parseInt(channel.getVariable("count"));
	    	
	    	if(readItemSoFar==0){
	    		int msgId=Integer.parseInt(request.getParameter("aid"));
	            
	            try{
	            	List<String> content=getMessageContent(msgId);
	            	int totalItem=content.size();
	            	channel.setVariable("totalItem",String.valueOf(totalItem));
	            	for(int i=0;i<totalItem;i++){
	            		String item="item"+i;
	            		channel.setVariable(item,(String)content.get(i));
	            	}
	            }
	            catch(Exception ex){
	            	logger.error("IMPORTANT:ERROR OCCURED WHILE IN CALL.CHECK THE ISSUE");
	            	logger.error("\n ERROR Caused by\n",ex);
	            	channel.hangup();
	            	return;
	            }
	    	}
	    	
	    	
	    	if(readItemSoFar>=Integer.parseInt(channel.getVariable("totalItem"))){
	    		channel.hangup();
	    		int par1=Integer.parseInt(request.getParameter("msgId"));	    		
	    		String serviceInfo=channel.getName();//Doubt
	    		CallSuccess obj=new CallSuccess(par1,aid,true,serviceInfo);
	    		obj.updateAlert(FOLLOWUP_TYPE);
	    		return;
	    	}
	    	
	    	updateCount(readItemSoFar);
	    	
	    	String itemNumber="item"+readItemSoFar;
	    	String itemContent=channel.getVariable(itemNumber);
	    	ContentFormat message=new FollowUpTemplate().parseString(itemContent);
	    
	    	String preferLanguage=(request.getParameter("language")).toLowerCase();
	    	String ttsNotation=request.getParameter("ttsNotation");
	    	
	    	if(message==null || (message.getContent())==null){
	    		requestFollowUpInfo();
	    		return;
	    	}
	    	
	    	//Caution:Ensure that the below if statement return.
	    	//Initially implementing onlt TTS_MODE. TODO: support for AUDIO_MODE
	    	if(message.getMode()==TTS_MODE){
	    		logger.info("Playing "+message.getContent()+" in TTS");
	    		channel.setVariable("message", message.getContent().toLowerCase());
	    		channel.setVariable("language",ttsNotation);
	    		//The escapeDigits, beepSeconds and numberOfTry should be read from properties
	    		String followUpInput = readInputUsingTTS(message.getContent().toLowerCase(), "#", "2", "3");
	    		
	    		//TODO: Add followUpInput validation here 
	    		setFollowUpResponse(followUpInput, aid);
	    		return;
	    	}
		}
		
	}


	/**
     * Persist user response to followUp questions
     * 
     * Handles patient incoming call.
     * 
     * Set patient number as pnumber
     * 
     * INCOMPLETE
     * 
     * @throws AgiException
     * 
     */
	private void setFollowUpResponse(String followUpInput, String aid){
		Session session = HibernateUtil.getSessionFactory().openSession();
		session.beginTransaction();
		String hql="from Alert where aid=:aid";
		Query query=session.createQuery(hql);
		query.setString("aid", aid);
		Alert alert = (Alert) query.list().get(0);
		
		alert.setresponse(followUpInput);
		session.update(alert);
		session.getTransaction().commit();
		session.close();

    	logger.info("Successfully updated response for alert "+aid);    	
	}


	private Patient getPatient(String aid){
		Session session = HibernateUtil.getSessionFactory().openSession();
		session.beginTransaction();
		String hql="from Alert where aid=:aid";
		Query query=session.createQuery(hql);
		query.setString("aid", aid);
		String pid =  query.list().get(0);
		session.getTransaction().commit();
    	session.close();
    	logger.info("Successfully retreived msg content from database with msgId"+msgId);
    	return pid;
	}



	/**
     * Use in Incoming-Call Context
     * 
     * Handles patient incoming call.
     * 
     * Set patient number as pnumber
     * 
     * INCOMPLETE
     * 
     * @throws AgiException
     * 
     */
    private void handleIncomingCall() throws AgiException,Exception{ 
    	
    	//Will be implemented in case patients are allowed to call our system to report followup params
    }

	/**
	 * Informs the patient that this is a followup call from Raxa and confirms patient identity
	 * 
	 * 
	 * 
	 * @param Patient
	 * @param defaultLanguage
	 * @return
	 * @throws AgiException
	 * @throws Exception
	 */
    private Boolean sayWelcomeAndConfirmPatient(Patient followUpPatient) throws AgiException,Exception{
    	
    	String allLanguageVoiceLocation=null,welcomeVoiceFileLocation=null;
		String allLanguageText,welcomeText;
    	String languagePlaying=null;boolean isPatientRegistered=true;
	
    	allLanguageText=getValueFromPropertyFile("allLangaugeText","english");
    	welcomeText=getValueFromPropertyFile("followUpWelcomeText","english");

    	char isPatientConfirmed = getOptionUsingTTS(welcomeText + followUpPatient.getPatientName(),"129","5000",2);
    	
    	
    	if(isPatientConfirmed == '1')
    		return true;
    	else
    		return false;	

    	//Implement code to list other patients associated with the phone number and check if the person
    	// who answered is also one of those patients who has a followup
    	
    }
   
   
    


	/**
     * Gets value from properties file.
     */
    private String getValueFromPropertyFile(String property,String propertyFile){
    	Properties prop = new Properties();
		try{
			logger.debug("Trying to fetch the folder property "+property+" from properties "+propertyFile+".properties");
			prop.load(this.getClass().getClassLoader().getResourceAsStream(propertyFile+".properties"));
			return prop.getProperty(property);
		}
		catch(Exception ex){
			logger.error("Some error occured while trying to fetch property "+property+" from properties "+propertyFile+".properties");
			logger.error("\nCaused by:\n",ex);
		}
		return null;
    }
    

    /**
     * Play any voice file in Location fileLocation
     * 
     * Caution:Don't give any extension. eg:/home/you/music/metallica/St.Anger
     * 
     * @param fileLocation
     * @throws AgiException
     */
    private void playFile(String fileLocation) throws AgiException{
    	channel.streamFile(fileLocation);
    }
    
   
    
    /**
     * Play the text in desired langauge and stops if get an option.If not play a beep and wait for 3 seconds.
     * escape String 
     * 
     * @param toSpeak
     * @param escapeDigits  is a set of digits,if pressed,will stop the voice playing and send the pressed digits to option variable
     * @param beepSeconds:  is time in milliseconds.After playing a beep the application will wait for beepSeconds for user to send an input
     * @param numberOfTry   if failed to get an option it will play the same thing for numberOfTry times
     * @return
     * @throws AgiException
     */
    private char getOptionUsingTTS(String toSpeak,String escapeDigits,String beepSeconds,int numberOfTry) throws AgiException{
    	char option='0';int count=0;
    	String ttsNotation=getTTSNotation(language);
    	String beepVoiceLocation=getValueFromPropertyFile("beep","english");
 //   	playUsingTTS("Please Press your option after the beep",ttsNotation);
    	Long time=Long.parseLong(beepSeconds);
    	while(option=='0'&& count<numberOfTry){
    		playUsingTTS(toSpeak,ttsNotation,escapeDigits);
    		String dtmfKey=channel.getVariable("option");
    		
    		if(!(dtmfKey==null)){
    			option=dtmfKey.charAt(0);
    			channel.setVariable("option", "0");
    		}
    		if(option=='0')
    			option=getOption(beepVoiceLocation,escapeDigits,time);
    		++count;
    	}
    	if(option=='0')
    		ifOptionNotChosenAfterManyTries();
    	
    	return option;
    	
    }
    
    /**
     * @throws AgiException 
     * 
     */
    

    /**
     * This method plays the sound file and wait for timeout(in millisecods) for the patient to press something.
     * 
     * @see '9' is reserved for some special function.For eg.If the patient presses 9 may be it will directly connect to
     * some human.
     * @param fileLocation:location of file to be streamed
     * @param timeout:time to wait to get option from the patient
     * @param numberOfrepeat:if patient does not select anything number of time to wait 
     * @param escapeDigits:will stop playing the file if pressed
     * @return Returns 0 if no digit being pressed, or the ASCII numerical value of the digit if one was pressed, 
     * or -1 on error or if the channel was disconnected.
     * @throws AgiException
     * 
     */
    /**
     * 
     * @param fileLocation  location where pre-recorded voice is stored
     * @param timeout		time(millisecond) to wait for patient to enter an option
     * @param escapeDigits	when pressed stops the voice file playing and take the input as user choice
     * @param numberOfRepeat number of times to repeat the voice file if user fails to press an option
     * @return
     * @throws AgiException
     */
    private char getPatientOption(String fileLocation,String timeout,String escapeDigits,int numberOfRepeat)throws AgiException{
    	//If patient press any of the escape digit the sound streaming will stop
    	String defaultTimeout="15000";	//hard coded
    	char option='0';int count=0;
    	if(timeout==null)
    		timeout=defaultTimeout;
    	Long time=Long.parseLong(timeout);
    	while(option=='0'&& count<numberOfRepeat){
    		option=channel.getOption(fileLocation,escapeDigits,time);
    		++count;
    	}
    	if(option=='0')
    		ifOptionNotChosenAfterManyTries();
    	
    	return option;
    }
    
   
    /**
     * Action to be taken when user doesnot choose any option
     * @throws AgiException
     */
    private void ifOptionNotChosenAfterManyTries() throws AgiException{
    	logger.error("Incoming Call:Patient didnot choose any option after many tries.HangingUp");
    	channel.hangup();
    }
    
   
    /**
     *  Return list of patient associated with a number
     * 
     * @param pnumber
     * @return
     */
    List<Patient> getAllPatientWithNumber(String pnumber){
    	List<Patient> nameAndId=new ArrayList<Patient>();
    	try{
	    	Session session = HibernateUtil.getSessionFactory().openSession();
	    	session.beginTransaction();
			String hql="from Patient where (pnumber=:pnumber or snumber=:pnumber) and pid in (select pid from PAlert where alertType=:alertType)";
	    	Query query=session.createQuery(hql);
	    	query.setString("pnumber", pnumber);
	    	query.setInteger("alertType", IVR_TYPE);
	    	List<Patient> patientList=(List<Patient>)query.list();
	    	if(patientList==null || patientList.size()<1)
	    		return null;
	    	for(int i=0;i<patientList.size();i++){
	    		Patient p=(Patient)patientList.get(i);
	    		nameAndId.add(p);
	    	}
	    	return nameAndId;
    	}
    	catch(Exception ex){
    		logger.error("unable to retrieve data for patient with phone number:"+pnumber);
    		logger.error("\nCaused by:\n",ex);
    		return null;
    	}
    }
    /**
     *  Use in Incoming-Call Context.
     * 
     * takes message and ttsNotation i.e (en,hi) and plays it
     * @param message
     * @param ttsNotation
     * @throws AgiException
     */
    private void playUsingTTS(String message,String ttsNotation) throws AgiException{
    	playUsingTTS(message,ttsNotation,null);
    	
    }
    
    /**
     * Play the message using TTS and stop laying it as soon as it encounters any escape digits
     * @param message
     * @param ttsNotation
     * @param escapeDigits
     * @throws AgiException
     * 
     */
    private void playUsingTTS(String message,String ttsNotation,String escapeDigits) throws AgiException{
    	if(message==null) return;
    	String contentToPlay;String comma=",";
    	String onlySpeed=comma+ttsNotation+comma+comma+speed;
    	String speedAndEscape=comma+ttsNotation+comma+escapeDigits+comma+speed;
    	
    	//eg.exec("AGI","google"hello Atul today you have to take",hi);
    	if(!ttsNotation.equals("en")){
    		//google Translate to translate first
    		//store that in a channel variable
    		//http://zaf.github.io/asterisk-googletranslate/
    		//Not Supported yet.Need API Registration.
    		String translate="googletranslate.agi,\""+message+"\""+comma+ttsNotation;
    		exec("AGI",translate);
    		message=channel.getVariable("gtranslation");
    	}
    	
    	if(escapeDigits==null)
    		contentToPlay="googletts.agi,\""+message+"\""+onlySpeed;
    	else 
    		contentToPlay="googletts.agi,\""+message+"\""+speedAndEscape;
    	
    	logger.info("Playing:"+contentToPlay);
    	
    	exec("AGI",contentToPlay);
    }
    
    /**
     *  Use in Incoming-Call Context
     * 
     *Copied from org.raxa.module.ami.Outgoing.getTTSNotation Method. Just to make the AGI completely independent. 
     *Return ttsNotation for preferLanguage else return default Notation
     */
    
    public String getTTSNotation(String preferLanguage){
    	String defaultLanguage=null;
    	Properties prop = new Properties();
		try{
			logger.info("Trying to fetch the notation for the prefer language:"+preferLanguage);
			prop.load(this.getClass().getClassLoader().getResourceAsStream("tts.properties"));
			defaultLanguage=prop.getProperty("default");
			return(prop.getProperty(preferLanguage.toLowerCase()));
		}
		catch(IOException ex) {
    		
    		logger.error("Unable to set prefer language:"+preferLanguage+" playing in defaultLanguage:"+defaultLanguage);
    		logger.error("\nCaused by:\n",ex);
    		return defaultLanguage;
        }
    }
   
    /**
     * used in Outgoing-Call Context.
     * 
     * update how many ivrMsg:itemNumber of msgId has been played.
     * @param count
     * @throws AgiException
     */
    private void updateCount(int count) throws AgiException{
		++count;
		channel.setVariable("count",String.valueOf(count));
	}
    
    /**
     * used in Outgoing-Call Context.
     * @param itemContent
     * @return
     */

   
	
	/**
	 * used in Outgoing-Call Context.
	 * 
     * Get Message Content form IvrMsg
     * @param msgId
     * @return
     * @throws Exception
     */
	private List<String> getMessageContent(int msgId) throws Exception{
		logger.info("Getting content for followup question haveing msgId"+msgId);
		String hql="select content from IvrMsg where ivrId=:msgId order by itemNumber";
		Session session = HibernateUtil.getSessionFactory().openSession();
    	session.beginTransaction();
    	Query query=session.createQuery(hql);
    	query.setInteger("msgId", msgId);
    	List<String> content=(List<String>)query.list();
    	session.getTransaction().commit();
    	session.close();
    	logger.info("Successfully retreived msg content from database with msgId"+msgId);
    	return content;
	}
	
    /**
     * Play the text in desired langauge and stops if get an option.If not play a beep and wait for 3 seconds.
     * escape String 
     * 
     * @param toSpeak
     * @param escapeDigits  is a set of digits,if pressed,will stop the voice playing and send the pressed digits to option variable
     * @param beepSeconds:  is time in milliseconds.After playing a beep the application will wait for beepSeconds for user to send an input
     * @param numberOfTry   if failed to get an option it will play the same thing for numberOfTry times
     * @return
     * @throws AgiException
     */
    private String readInputUsingTTS(String toSpeak,String escapeDigits,String beepSeconds,int numberOfTry) throws AgiException{
    	char option='0';int count=0;
    	String ttsNotation=getTTSNotation(language);
    	String beepVoiceLocation=getValueFromPropertyFile("beep","english");
 //   	playUsingTTS("Please Press your option after the beep",ttsNotation);
    	Long time=Long.parseLong(beepSeconds);
    	while(count<numberOfTry){
    		playUsingTTS(toSpeak,ttsNotation,escapeDigits);
    		String dtmfKey=channel.getVariable("option");
    		
    		if(!(dtmfKey==null)){
    			return dtmfkey;
    		}
    		else
    			++count;
    	}
   
    	ifOptionNotChosenAfterManyTries();
    	
    	return dtmfkey;
    	
    }	
    
}
    
 
