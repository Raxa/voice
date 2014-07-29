
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
import org.raxa.database.FollowupChoice;
import org.raxa.database.FollowupQstn;
import org.raxa.database.FollowupResponse;
import org.raxa.database.HibernateUtil;
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
public class FollowupCallHandler extends CallHandler
{
	private AgiRequest request;
	AgiChannel channel;
	private Logger logger = Logger.getLogger(this.getClass());
	String pid;
	FollowupCallHandler(AgiRequest request, AgiChannel channel, String language, String pid){
		super(channel,request,language);
		this.request=request;
	    this.channel=channel;
		this.pid = pid;
		try{
    		service(request, channel);
			} catch(Exception ex){
				logger.error("\nCaused by:\n",ex);
			}	}
	

    
	  /**
	 * checks whether the call is incoming or outgoing.Handles the call accordingly
	 */
    public void service(AgiRequest request, AgiChannel channel) throws AgiException{
	requestFollowUpInfo();
	}
	

  /**
   * Request followup information from patient'
   * 
   * Presently implemented using Asterisk DialPlan similar to CallHandler for reminders.
   * Can alternatively use getData() provided by BaseAGIScript to read input 
   * 
   * 
   * INCOMPLETE
   * 
   * @throws AgiException
   * 
   */
	private void requestFollowUpInfo() {
        try{
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        //Get followup question
        //TODO: modify query to search based on patient id
        String hqlQstn = "from FollowupQstn where fid=:fid";
        Query queryQstn = session.createQuery(hqlQstn);
        Integer fid = 1;
        queryQstn.setInteger("fid", fid);
        FollowupQstn followupQstn = (FollowupQstn) queryQstn.list().get(0);
        String qstn = followupQstn.getQstn();
        String ttsNotation=getTTSNotation(language);
        char option;
        boolean hasRecognized = false;
        //Play follow up question
        playUsingTTS(qstn,ttsNotation);

        //Get followup choices
        String hqlChoice = "from FollowupChoice where fid=:fid";
        Query queryChoice = session.createQuery(hqlChoice);
        queryChoice.setInteger("fid", fid);
        List<FollowupChoice> followupChoices = (List<FollowupChoice>) queryChoice.list();
        FollowupResponse followupResponse = null;

        //get user response using ASR
        String choice = getOptionUsingAsr(2);
        logger.info("you said "+choice);
        for(FollowupChoice followupChoice : followupChoices){
        	if(choice.equals(followupChoice.getOption().toLowerCase())){
        		hasRecognized = true;
        		followupResponse = setFollowUpResponse(followupChoice.getFcid(), fid);
        		break;
        	}
        }
        
        if(hasRecognized == false)
        {
        //ASR failed. Get followup options using DTMF
        int count = 1;
        String dtmfMenuText = "press";
        String dtmfDigits = "";
        for(FollowupChoice followupChoice : followupChoices){
            dtmfMenuText += " "+count+" for "+followupChoice.getOption();
            dtmfDigits += ""+count;
            count++;
        }
        option=getOptionUsingTTS(dtmfMenuText,dtmfDigits,"5000",2);
        followupResponse = setFollowUpResponse(followupChoices.get(Character.getNumericValue(option)).getFcid(), fid);
        }
        
        if(followupResponse != null){
            session.save(followupResponse);
            logger.info("Successfully saved response for followup "+fid);
            session.getTransaction().commit();
            session.close();
            String recordSuccessMsg = getValueFromPropertyFile("recordSuccessMsg", "english"); 
            playUsingTTS(recordSuccessMsg,ttsNotation);	
         }
        else{
        	logger.info("Failed to get response for followup "+fid);
        	String followupFailMsg = getValueFromPropertyFile("followupFailMsg", "english");
        	playUsingTTS(followupFailMsg,ttsNotation);
         }
        }
        catch(Exception ex){
            ex.printStackTrace();
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
   * @throws AgiException
   * 
   */
	private FollowupResponse setFollowUpResponse(int fcid, int fid){
	FollowupResponse followupResponse = new FollowupResponse();
	followupResponse.setFid(fid);
	followupResponse.setResponse(fcid);
	java.util.Date date= new java.util.Date();
	Timestamp now = new Timestamp(date.getTime());
	followupResponse.setDate(now);
	followupResponse.setExecuted(true);
	followupResponse.setLastTry(now);
	followupResponse.setRetryCount(0);
	followupResponse.setSyncStatus(false);
	return followupResponse;
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
}