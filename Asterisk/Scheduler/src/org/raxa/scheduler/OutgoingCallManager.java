
package org.raxa.scheduler;
import java.io.IOException;
import org.asteriskjava.manager.AuthenticationFailedException;
import org.asteriskjava.manager.ManagerConnection;
import org.asteriskjava.manager.ManagerConnectionFactory;
import org.asteriskjava.manager.TimeoutException;
import org.asteriskjava.manager.action.OriginateAction;
import org.asteriskjava.manager.response.ManagerResponse;
import org.apache.log4j.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Call Patient Number and set context to handle the call via AGI
 *
 *
 * @author atul
 *
 */
public class OutgoingCallManager{
	/**
	 * Connects Java to Asterisk
	 */
    private ManagerConnection managerConnection;
    /**
     * context name(in extensions.conf) which will handle call
     */
    private String CONTEXT;
    /**
     * Caller ID:is visible if patient is using softphone
     */
    private String CALLERID;
    /**
     * 
     */
    private Long TIMEOUT;
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
     * Extension in the context where the call should be redirected
     */
    private String EXTENSION;
    /**
     * Alert ID of the alert to be said
     */
    private String aid;
    Logger logger = Logger.getLogger(OutgoingCallManager.class);
    
    public OutgoingCallManager(){
    	   setProperties();
    	   

    	   ManagerConnectionFactory factory = new ManagerConnectionFactory(
    			   ASTERISK_SERVER_URL, MANAGER_USERNAME, MANAGER_PASSWORD);

           this.managerConnection = factory.createManagerConnection();
           
    }
    
    public void setProperties(){
        System.out.println("getLevel(logger) = " + logger.getLevel());
        logger.setLevel(Level.DEBUG);
        System.out.println("getLevel(logger) = " + logger.getLevel());
 	    ASTERISK_SERVER_URL=null;
 	    MANAGER_USERNAME=null;
 	    MANAGER_PASSWORD=null;
 	    CONTEXT=null;
 	    CALLERID=null;
 	    TIMEOUT=null;
 	    EXTENSION=null;
 	   try {
 		   	Properties prop = new Properties();
    		prop.load(OutgoingCallManager.class.getClassLoader().getResourceAsStream("config.properties"));
    		ASTERISK_SERVER_URL=prop.getProperty("Asterisk_URL");
    		MANAGER_USERNAME=prop.getProperty("Manager_Username");
    		MANAGER_PASSWORD=prop.getProperty("Manager_Password");
    		CONTEXT=prop.getProperty("MedRemind_Context");
     	    CALLERID=prop.getProperty("MedRemind_CallerId");
     	    TIMEOUT=Long.parseLong(prop.getProperty("MedRemind_TimeOut"),10);
     	    EXTENSION=prop.getProperty("MedRemind_Extension");
    	   } 
    	catch (IOException ex) {
    		
    		logger.error("Some error occur while retreiving information from config.properties.Unable to forward call to asterisk");
    		logger.error("\nCaused by\n",ex);
        }
    }
    
    
    public void setContext(String s){
    	CONTEXT=s;
    }
    
    public void setCallerId(String s){
    	CALLERID=s;
    }
    
    public void setTimeout(Long l){
    	TIMEOUT=l;
    }
    		
    /**
     * Tell Asterisk to call a number:pnumber and give control of the call to "CONTEXT:EXTENSION".
     * Set channel variables which is passed to AGI in extensions.conf
     * @param pnumber
     * @param msgId
     * @param aid
     * @param preferLanguage
     * @return
     */
    public boolean callPatient(String pnumber,String msgId,String aid,String preferLanguage){
    	this.aid=aid;
        logger.debug("Placing the call to patient with phone number-"+pnumber+" having alertId-"+aid+" and msgId-"+msgId+" and preferLanguage "+preferLanguage);
    	pnumber="SIP/1000abc"; 							//Should be deleted.only for testing purpose
    	Map<String,String> var=new HashMap<String,String>();
    	var.put("msgId",msgId);
    	var.put("aid",aid);
    	var.put("preferLanguage", preferLanguage.toLowerCase());
    	var.put("ttsNotation", getTTSNotation(preferLanguage));
    		
    	try{
	        OriginateAction originateAction=new OriginateAction();
	        ManagerResponse originateResponse=new ManagerResponse();
	        managerConnection.login();
	        originateAction = new OriginateAction();
	        originateAction.setCallerId(CALLERID);
	        originateAction.setChannel(pnumber);        //I M P O R T A N T :- should be updated dahdi/go/pnumber
	        originateAction.setContext(CONTEXT);
	        originateAction.setExten(EXTENSION);
	        originateAction.setPriority(new Integer(1));
	        originateAction.setTimeout(TIMEOUT);
	        originateAction.setAsync(true);
	        originateAction.setVariables(var);
	        originateResponse = managerConnection.sendAction(originateAction,10000);
	        logger.info("Asterisk response for call to phone-"+pnumber);
	        logger.info(originateResponse.getResponse());
	        managerConnection.logoff();
	        return true;
        
    	}
    	catch(AuthenticationFailedException ex){
    		logger.error("In org.raxa.module.ami.Outgoing.java:Authentication Failure");
    		return false;
    	}
    	catch(TimeoutException ex){
    		logger.error("In org.raxa.module.ami.Outgoing.java:TimeOut Exception");
    		return false;
    	}
    	catch(Exception ex){
    		logger.error("In org.raxa.module.ami.Outgoing.java:Some Error Occured");
    		logger.error("\nCaused by\n",ex);
    		return false;
    	}
    }
    
    /**
     * here if TTS does not support the prefer Language,It will pass English as defaultLanguage.But then the header and footer should be played
     * in default language if the mode is AudioFolder.all language should be in lower case in prop file.
     * @param preferLangaue:Language that need to converted to voice by TTS.
     * @return short notation of the Langauge understood by the TTS.
     */
    public String getTTSNotation(String preferLanguage){
    	String defaultLanguage=null;
    	Properties prop = new Properties();
		try{
			logger.debug("Trying to fetch the notation for the prefer language:"+preferLanguage);
			prop.load(this.getClass().getClassLoader().getResourceAsStream("tts.properties"));
			defaultLanguage=prop.getProperty("default");
			return(prop.getProperty(preferLanguage.toLowerCase()));
		}
		catch(IOException ex) {
    		
    		logger.error("Unable to set prefer language:"+preferLanguage+" playing in defaultLanguage:"+defaultLanguage+" for alert Id:"+aid);
    		logger.error("\nCaused by\n",ex);
    		return defaultLanguage;
        }
    }
 }
