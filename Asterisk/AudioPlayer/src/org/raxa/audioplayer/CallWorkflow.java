/*

 */

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

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
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
import org.raxa.alertmessage.ContentFormat;
import org.raxa.alertmessage.MedicineInformation;
import org.raxa.alertmessage.MessageTemplate;
import org.raxa.database.FollowupChoice;
import org.raxa.database.FollowupQstn;
import org.raxa.database.FollowupResponse;
import org.raxa.database.HibernateUtil;
import org.raxa.database.Patient;
import org.raxa.registration.Register;
import org.raxa.alertmessage.MessageInterface;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.raxa.database.VariableSetter;

/**
 * Outgoing Call Context here sets the following channel variable
 * totalItem;item0,item1....,count
 * 
 * CAUTION:Even if the patient has hung up the program is going to execute until it meets an exception or termination.
 * @author atul
 *
 */
public class CallWorkflow extends BaseAgiScript implements MessageInterface,VariableSetter
{
	private AgiRequest request;
	AgiChannel channel;
	private Logger logger = Logger.getLogger(this.getClass());
	String language;
	String pnumber;
	String pid;
	final String speed="1";							//The default speed to play voice file 
	String ttsNotation;
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
    /**
     *  gets list of patients with the same pnumber
     */
    List<Patient> patientList;
	
	private DefaultCallHandler defaultCallHandler;
    
	public CallWorkflow(AgiChannel channel, String language, String pid) {
		super();
		this.channel = channel;
		this.language = language;
		this.pid = pid;
	}

	public CallWorkflow(){
		
	}
	
    /**
	 * checks whether the call is incoming or outgoing.Handles the call accordingly
	 */
    public void service(AgiRequest request, AgiChannel channel) throws AgiException{

    	try{
    		String defaultLanguage=getValueFromPropertyFile("0","languageMap");
            language=defaultLanguage;
    		pnumber=null;
            LogManager.getRootLogger().setLevel(Level.DEBUG);
			defaultCallHandler = new DefaultCallHandler(request,channel,language);
			defaultCallHandler.answer();
	    	this.request=request;
	    	this.channel=channel;
			//TODO: Use config instead of hardcoded values 
	    	if(request.getContext().equals("incoming-call"))
	    		handleIncomingCall();
	        else if(request.getContext().equals("outgoing-call"))
	        	provideMedicalInfo();
	        else if(request.getContext().equals("outgoing-followup-call"))
	        	{
				int fid = Integer.parseInt(channel.getVariable("fid"));
				new FollowupCallHandler(request,channel,language,fid);
				}
	        return;
    	}
    	catch(AgiException ex){
    		
    		logger.error("IMPORTANT:SOME ERROR WHILE HANDLING THE CALL with caller number:"+pnumber);
    		logger.error(ex.getStackTrace());
    		logger.info("Hanging the call");
    		hangup();
    	}
    	catch(Exception ex1){
    		
    		logger.error("IMPORTANT:SOME ERROR WHILE HANDLING THE CALL with caller:"+pnumber);
    		logger.error(ex1.getStackTrace());
    		logger.info("Hanging  the call");
    		hangup();
    	}
    	
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
    	
    	String defaultLanguage = null;
    	//pnumber=channel.getName();		//IMPORTANT  DEPEND ON THE TRIE SERVICE WE WILL BE USING
    	//HARDCODE HERE 
    	pnumber="SIP/1000abc";
    	patientList=getAllPatientWithNumber(pnumber);
		pid = "3a3d33e6-80f4-4057-b94e-079f364d3c17";
		new AppointmentCallHandler(request, channel,language, pid);
		
    	String languagePlaying=sayWelcomeAndgetLanguage(patientList,defaultLanguage);
    	//String languagePlaying="english";
    	if(languagePlaying==null){
    		logger.error("Unable to get Language when playing to user withn number "+pnumber);
    		return;
    	}
    	language=languagePlaying;
    	channel.setVariable("numberOftimesMenuPlayed", "0");
    	channel.setVariable("reminderMenuPlayCount", "0");
    	//playReminderMenu(patientList, languagePlaying);
    	playMainMenu();
    	//Done What patient wanted.
    }

	/**
	 * Choose a random prefer Language to be played to the patient if patient is registered.If not registered plays welcome with the default language.
	 * 
	 * Ask patient if he is comfortable with current language(1 for yes 2 for no).If not,provide him with a set of language(to be recorded manually)which TTS support.
	 * Enough care should be taken to map patient option with the language in langaugeMap.properties.(languageMap and voice File must go hand in hand.)
	 * 
	 * 
	 * @param patientList
	 * @param defaultLanguage
	 * @return
	 * @throws AgiException
	 * @throws Exception
	 */
    private String sayWelcomeAndgetLanguage(List<Patient> patientList,String defaultLanguage) throws AgiException,Exception{
    	
    	String allLanguageVoiceLocation=null,welcomeVoiceFileLocation=null;
		String allLanguageText,welcomeText;
    	String languagePlaying=null;boolean isPatientRegistered=true;
	
    	allLanguageText=getValueFromPropertyFile("allLangaugeText","english");
    	welcomeText=getValueFromPropertyFile("welcomeText","english");
    	if(patientList==null || patientList.size()==0){
    		isPatientRegistered=false;
    		languagePlaying=defaultLanguage;
    	}
    	else{
    		languagePlaying=patientList.get(0).getPatientPreferredLanguage();
    		if(languagePlaying!=null)
    			language=languagePlaying;
    		//If we want the welcome to be played by pre recorded voice unmark it
   /* 		welcomeVoiceFileLocation=getValueFromPropertyFile(languagePlaying.toLowerCase(),"welcome");
    		if(welcomeVoiceFileLocation==null){
    			languagePlaying=defaultLanguage;
    			welcomeVoiceFileLocation=getValueFromPropertyFile(languagePlaying.toLowerCase(),"welcome");
    		}
    */	}
    	//plays welcome in the 'languagePlaying' language.Should say "Welcome to Raxa.Do
		// you want to continue with this Language.1 for yes 2 for no."
    	//See "continueWithLanguage.properties" for any change			
    	//char continueWithTheLanguage=getPatientOption(welcomeVoiceFileLocation,null,"129",1);
    	char continueWithTheLanguage = defaultCallHandler.getOptionUsingTTS(welcomeText,"129","5000",2);
    	String isLanguageChanged=analysePatientOption("continueWithLanguage",continueWithTheLanguage);
    	if(isLanguageChanged==null || !Boolean.parseBoolean(isLanguageChanged)){
    		//char whichLanguage=getPatientOption(allLanguageVoiceLocation,"20000","1234569",2);			//SHOULD BE PLAYED IN DIFFERENT LANGUAGE AND SHOULD BE PRERECORDED
    		char whichLanguage= defaultCallHandler.getOptionUsingTTS(allLanguageText,"1234569","5000",2);	//SHOULD BE DELETED
    		languagePlaying=analysePatientOption("languageMap",whichLanguage);
    	}
    	//When Patient doesnot respond for any language even after playing it a number of times.
    	if(languagePlaying==null){
			channel.hangup();
			logger.error("Cannot map the language chosen by user");
			return null;
		}
    	
    	return languagePlaying;
    }
    /**
     * Plays the main menu using TTS.The main menu String is fetched from mainmenutext property file in english.properties.
     * 
     * 
     * @throws Exception
     */
    private void playMainMenu() throws Exception{
    	String mainmenuVoiceFileLocation=getValueFromPropertyFile(language.toLowerCase(),"mainmenu");
    	String mainmenuText=getValueFromPropertyFile("mainMenuText","english");
    	
    	channel.setVariable("numberOftimesMenuPlayed", String.valueOf((Integer.parseInt(channel.getVariable("numberOftimesMenuPlayed")))+1));
    	
    					//char option=getPatientOption(mainmenuVoiceFileLocation,"11000","1234569",2);
    	char option= defaultCallHandler.getOptionUsingTTS(mainmenuText,"1234569","5000",2);
		logger.debug("U chose " + option);
    	String KeyWord=analysePatientOption("mainmenu",option);
    	if(KeyWord==null){
    		channel.hangup();
    		logger.error("Unable to get what patient opted in main menu of language "+language);
    		return;
    	}
    	
    	doWhatPatientWant(KeyWord,patientList,language);
    	
    }
    /**
     * Takes the Keyword and all the possible patientList associated with the phone number and patient prefer langauge
     * Checks if the keyword require pid?
     * If yes,ask for the patietn identity among patientList.
     * Stores the pid and fetch asked information using the pid
     * 
     * @param keyWord
     * @param patientList
     * @param language
     * @throws AgiException
     * @throws Exception
     */
    public void doWhatPatientWant(String keyWord,List<Patient> patientList,String language) throws AgiException,Exception{
    	boolean isPatientRegistered=true;boolean isAllowed=true;
    	int count=Integer.parseInt(channel.getVariable("numberOftimesMenuPlayed"));
    	
    	if(patientList==null || patientList.size()==0)
    		isPatientRegistered=false;
    	//All option that needs to check if the patient is in the alert System already should add that option to the or Statement
    	//This is to deal with fact that if a patient is not register and he keeps on opting for the option that needs patient to be registered
    	else if(keyWord.toLowerCase().equals("reminder")){
    			new ReminderCallHandler(request,channel,patientList, language);
    	}
    	else
		{	
			pid = defaultCallHandler.getPid(patientList);
			if(keyWord.toLowerCase().equals("followup")){
    			new FollowupCallHandler(request,channel,language,pid);
    		}
    		else if(keyWord.toLowerCase().equals("call")){
			new InteractionCallHandler(request,channel,language,pid);
    		}
		  	else if(keyWord.toLowerCase().equals("appointments")){
    		new AppointmentCallHandler(request, channel,language,pid);
    		}
    		else if(keyWord.toLowerCase().equals("hangup")){
    			channel.hangup();
    		}
		}
    	playMainMenu();
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
    public char getPatientOption(String fileLocation,String timeout,String escapeDigits,int numberOfRepeat)throws AgiException{
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
    		defaultCallHandler.ifOptionNotChosenAfterManyTries();
    	
    	return option;
    }

    
    /**
     * search for keyword from a property file with option as property field
     */
    
    public String analysePatientOption(String propertyFile,char option){
    	if(option<='8' && option>='1'){
    		String keyWord=getValueFromPropertyFile(Character.toString(option),propertyFile);
    		return keyWord;
    	}
    	return null;
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
     * used in Outgoing-Call Context.
     * 
     * It loops between AGI and asterisk dial plan outgoing-call context.
     * asterisk dial plan contains google TTS.
     * If asked to play using TTS it returns and play it via google TTS.
     * If asked to play using audio folder it loops back using while.
     * 
     * It does use recursion.
     * 
     * Warning:Unless you are familiar with asterisk dial plan don't mess with it.
     * @throws AgiException
     */
   
    private void provideMedicalInfo() throws AgiException{
    	
    	while(true){
	    	int readItemSoFar=Integer.parseInt(channel.getVariable("count"));
	    	
	    	if(readItemSoFar==0){
	    		int msgId=Integer.parseInt(request.getParameter("msgId"));
	            
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
	    		String aid=request.getParameter("aid");
	    		String serviceInfo=channel.getName();//Doubt
	    		CallSuccess obj=new CallSuccess(par1,aid,true,serviceInfo);
	    		obj.updateAlert(IVR_TYPE);
	    		return;
	    	}
	    	
	    	defaultCallHandler.updateCount(readItemSoFar);
	    	
	    	String itemNumber="item"+readItemSoFar;
	    	String itemContent=channel.getVariable(itemNumber);
	    	ContentFormat message=new MessageTemplate().parseString(itemContent);
	    
	    	String preferLanguage=(request.getParameter("language")).toLowerCase();
	    	String ttsNotation=request.getParameter("ttsNotation");
	    	
	    	if(message==null || (message.getContent())==null){
	    		provideMedicalInfo();
	    		return;
	    	}
	    	
	    	//Caution:Ensure that the below if statement return.
	    	
	    	if(message.getMode()==TTS_MODE){
	    		logger.info("Playing "+message.getContent()+" in TTS");
	    		channel.setVariable("message", message.getContent().toLowerCase());
	    		channel.setVariable("language",ttsNotation);
	    		return;
	    	}
	    	//The part below does not depend on dialplan.So you can mess with it..
	    	
	    	else if(message.getMode()==AUDIO_MODE){
		    		Properties prop = new Properties();
		    		try{
		    			String fileLocation;
		    			logger.info("Searching for "+preferLanguage+".properties file");
			    		prop.load(this.getClass().getClassLoader().getResourceAsStream(preferLanguage+".properties"));
			    		if(message.getField().equals(new MessageTemplate().DoseFieldRepresentation))
			    			fileLocation=prop.getProperty(message.getField())+"/"+message.getContent(); 
			    		else fileLocation=prop.getProperty(message.getField())+"/"+message.getField();    //if want to put un fromatted location then remove "/"+message.getField()
			    		
			    		logger.info("Playing "+message.getField()+" in from audio Folder with file location "+fileLocation);
			    		channel.streamFile(fileLocation);
			    	}
		    		catch (IOException ex) {
		        		
		        		logger.error("Some error while playing AudioFile returning back");
		        		logger.error("\nCaused by:\n",ex);
		            }
		    }
    	}
    }
    
   
	
	/**
	 * used in Outgoing-Call Context.
	 * 
     * Get Message Content form IvrMsg
     * @param msgId
     * @return
     * @throws Exception
     */
	private List<String> getMessageContent(int msgId) throws Exception{
		logger.info("Getting content for medicine Reminder haveing msgId"+msgId);
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
	
	
	 
}
    
 
