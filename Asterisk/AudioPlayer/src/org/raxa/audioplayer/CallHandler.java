
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
import org.raxa.database.HibernateUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.raxa.database.Patient;
import org.raxa.database.VariableSetter;

/**
 * Outgoing Call Context here sets the following channel variable
 * totalItem;item0,item1....,count
 * 
 * CAUTION:Even if the patient has hung up the program is going to execute until it meets an exception or termination.
 * @author atul
 *
 */
public class CallHandler extends BaseAgiScript implements MessageInterface,VariableSetter
{
	protected AgiRequest request;
	protected AgiChannel channel;
	public String language;
	/**
	 * Connects Java to Asterisk
	 */
    private ManagerConnection managerConnection;
    /**
     * URL Where Asterisk is hosted
     */
    private String ASTERISK_SERVER_URL;
    /**
     * User Name of the Manager(defined in manager.conf)
     */
    private String MANAGER_USERNAME;
    /**
     * password of the Manager(defined in manager.conf)
     */
    private String MANAGER_PASSWORD;
	
	String ttsNotation;
	String pnumber;
	private Logger logger = Logger.getLogger(this.getClass());

	public CallHandler(AgiChannel channel, AgiRequest request, String language) {
	    	this.request=request;
	    	this.channel=channel;
			this.language = language;
			ttsNotation = getTTSNotation(language);
		    //pnumber=channel.getName();		//IMPORTANT  DEPEND ON THE TRIE SERVICE WE WILL BE USING
    		//HARDCODE HERE 
    		pnumber="SIP/1000abc";
	}
	
	public CallHandler(AgiChannel channel, AgiRequest request) {
	    	this.request=request;
	    	this.channel=channel;
		    //pnumber=channel.getName();		//IMPORTANT  DEPEND ON THE TRIE SERVICE WE WILL BE USING
    		//HARDCODE HERE 
    		pnumber="SIP/1000abc";
	}

	
	public CallHandler() {
	}
	
	  /**
	 * checks whether the call is incoming or outgoing.Handles the call accordingly
	 */
    public void service(AgiRequest request, AgiChannel channel) throws AgiException{
	
	}

	public char getOptionUsingTTS(String toSpeak,String escapeDigits,String beepSeconds,int numberOfTry) throws AgiException{
    	char option='0';int count=0;
    	String ttsNotation= "en";
		 //getTTSNotation(language);
    	String beepVoiceLocation="beep";
		 //getValueFromPropertyFile("beep","english");
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
    	//if(option=='0')
    		//ifOptionNotChosenAfterManyTries();
    	
    	return option;
    	
    }
	
	
	 /**
     *  Use in Incoming-Call Context.
     * 
     * takes message and ttsNotation i.e (en,hi) and plays it
     * @param message
     * @param ttsNotation
     * @throws AgiException
     */
    public void playUsingTTS(String message,String ttsNotation) throws AgiException{
    	playUsingTTS(message,ttsNotation,null);
	}
	 /**
     * Play the message using TTS and stop playing it as soon as it encounters any escape digits
     * @param message
     * @param ttsNotation
     * @param escapeDigits
     * @throws AgiException
     * 
     */
    public void playUsingTTS(String message,String ttsNotation,String escapeDigits) throws AgiException{
    	if(message==null) return;
    	String contentToPlay;String comma=",";
		String speed="1";
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
     * Gets value from properties file.
     */
    public String getValueFromPropertyFile(String property,String propertyFile){
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
     * Action to be taken when user doesnot choose any option
     * @throws AgiException
     */
    public void ifOptionNotChosenAfterManyTries() throws AgiException{
    	logger.error("Incoming Call:Patient didnot choose any option after many tries.HangingUp");
    	channel.hangup();
    }
    
	 /**
     *  Create and initialize manager connection
     * 
     */
    public void initManagerConnection(){
    	//managerConnection = new ManagerConnection();
    	ASTERISK_SERVER_URL=null;
  	    MANAGER_USERNAME=null;
  	    MANAGER_PASSWORD=null;
  	   try {
  		   	Properties prop = new Properties();
     		prop.load(CallHandler.class.getClassLoader().getResourceAsStream("config.properties"));
     		ASTERISK_SERVER_URL=prop.getProperty("Asterisk_URL");
     		MANAGER_USERNAME=prop.getProperty("Manager_Username");
     		MANAGER_PASSWORD=prop.getProperty("Manager_Password");
     	   } 
     	catch (IOException ex) {
     		
     		logger.error("Some error occur while retreiving information from config.properties. Unable to create manager instance");
     		logger.error("\nCaused by\n",ex);
    }
  	 ManagerConnectionFactory factory = new ManagerConnectionFactory(
		   ASTERISK_SERVER_URL, MANAGER_USERNAME, MANAGER_PASSWORD);
  	 this.managerConnection = factory.createManagerConnection();	   
   }	   
    
public String getOptionUsingAsr(int retryCount){
	  String audioPath = "/var/lib/asterisk/sounds/";
      String audioFileName = "";
      String msgBody = "";
      String response = "";
      double confidence = 0.0;
      
	try{
        channel.exec("Record","response%d:wav,2,5,");
        audioFileName = channel.getVariable("RECORDED_FILE");
    }
    catch(AgiException e){
        logger.error("Unable to record audio. AGI exception");
        logger.error("\nCaused by\n",e);
    }
	
    String accessToken = getAsrAccesToken();
    WitSpeech client = new WitSpeech(accessToken,"audio/wav");

    //TODO: Read path and file name from config

	File file = new File(audioPath+audioFileName+".wav");
	FileInputStream fis = null;
	logger.info("audio file is "+audioFileName);
		try {
			fis = new FileInputStream(file);
 
			System.out.println("Total file size to read (in bytes) : "
					+ fis.available());
 
			 response =  client.getResponse(fis);
             logger.info("server response is "+response);
         //return response;
         WitResponse witResponse = client.processWitResponse(response);
         msgBody = witResponse.getBody();
         confidence = witResponse.getOutcome().get_confidence();
         logger.info("message body is "+msgBody);
         

 
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e){
		 e.printStackTrace();
		}
		finally {
			try {
				if (fis != null)
					fis.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
			if((msgBody == "" || confidence < 0.5) && retryCount > 0)
			{
				retryCount--;
				try{
				playUsingTTS("Unable to understand. Please repeat your answer", ttsNotation);
				} catch (AgiException ae){
					ae.printStackTrace();
				}
				msgBody = getOptionUsingAsr(retryCount);
			}
		}
        return msgBody;
	}

	 /**
	 *  Read access token from properties
	 * 
	 */
	public String getAsrAccesToken(){
	String accessToken = null;
	Properties prop = new Properties();
	try{
		logger.info("Trying to get asr access token");
		prop.load(this.getClass().getClassLoader().getResourceAsStream("asr.properties"));
		accessToken = prop.getProperty("access-token");
		return accessToken;
	}
	catch(IOException ex) { 		
		logger.error("Unable to get asr access token");
		return "";
    }
   }
	
    /**
     * used in Outgoing-Call Context.
     * 
     * update how many ivrMsg:itemNumber of msgId has been played.
     * @param count
     * @throws AgiException
     */
    public void updateCount(int count) throws AgiException{
		++count;
		channel.setVariable("count",String.valueOf(count));
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
     * search for keyword from a property file with option as property field
     */
    
    public String analyseOption(String propertyFile,char option){
    	if(option<='8' && option>='1'){
    		String keyWord=getValueFromPropertyFile(Character.toString(option),propertyFile);
    		return keyWord;
    	}
    	return null;
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
		//infoMessage is the message informing patient to confirm his/her name
		String infoMessage=getValueFromPropertyFile("patientNameMessage","english");
		//following messages are the grammer to be inserted between consecutive names
    	String message1=getValueFromPropertyFile("patientNameMessage1","english");
    	String message2=getValueFromPropertyFile("patientNameMessage2","english");
    	ttsNotation = getTTSNotation(language);
		//play infoMessage
		playUsingTTS(infoMessage,ttsNotation);
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