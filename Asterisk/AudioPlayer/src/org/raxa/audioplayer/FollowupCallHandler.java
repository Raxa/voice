
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
 * All followup call handling methods
 * 
 * @author rahulr92
 *
 */
public class FollowupCallHandler extends CallHandler
{
	private AgiRequest request;
	AgiChannel channel;
	private Logger logger = Logger.getLogger(this.getClass());
	String pid;
	int fid;
	boolean HAS_FID;
	FollowupCallHandler(AgiRequest request, AgiChannel channel, String language, String pid){
		super(channel,request,language);
		this.request=request;
	    this.channel=channel;
		this.pid = pid;
		HAS_FID = false;
		try{
    		service(request, channel);
		} 
		catch(Exception ex){
				logger.error("\nCaused by:\n",ex);
		}	
	}
	

    FollowupCallHandler(AgiRequest request, AgiChannel channel, String language, int fid){
		super(channel,request,language);
		this.request=request;
	    this.channel=channel;
		this.fid = fid;
		HAS_FID = true;
		try{
    		service(request, channel);
			} catch(Exception ex){
				logger.error("\nCaused by:\n",ex);
			}	
	}
	

	  /**
	 * checks whether the call is incoming or outgoing.Handles the call accordingly
	 */
    public void service(AgiRequest request, AgiChannel channel) throws AgiException{
	requestFollowUpInfo();
	}
	

  /**
   * Request followup information from patient
   * Used in both incoming and outgoing context
   * in case of incoming call, HAS_FID will be false. we use the pid to get all followups questions for patient
   * in case of outgoing call, HAS_FID will be true. The fid of the question to ask will be passed by the Scheduler (OutgoingCallManager)
   * 
   * @throws AgiException
   * 
   */
	private void requestFollowUpInfo() {
	Session session = null;
    try{
        session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
		String ttsNotation=getTTSNotation(language);
        //Get followup question
		String hqlQstn;
		Query queryQstn;
        //TODO: modify query to search based on patient id
		if(HAS_FID)
		{
		hqlQstn = "from FollowupQstn where fid=:fid";
        queryQstn = session.createQuery(hqlQstn);
        queryQstn.setInteger("fid", fid);
		}
		else{
			//Find followup scheduled for this patient in the next one hour
			//time configurable
			hqlQstn = "from FollowupQstn where pid=:pid and fromDate <= CURRENT_DATE() and toDate >= CURRENT_DATE()";
			queryQstn = session.createQuery(hqlQstn);
			queryQstn.setString("pid", pid);
		}
        List<FollowupQstn> followupQstns = (List<FollowupQstn>) queryQstn.list();
		if(followupQstns.size() > 1){
			String noOfQstnInfo = "You have "+followupQstns.size()+" followup questions today.";
			playUsingTTS(noOfQstnInfo,ttsNotation);
		}
		for(FollowupQstn followupQstn : followupQstns){
			String qstn = followupQstn.getQstn();
			if(!HAS_FID){
				fid = followupQstn.getFid();
			}
			//Play follow up question
			playUsingTTS(qstn,ttsNotation);
			//Get followup choices
			String hqlChoice = "from FollowupChoice where fid=:fid";
			Query queryChoice = session.createQuery(hqlChoice);
			queryChoice.setInteger("fid", fid);
			List<FollowupChoice> followupChoices = (List<FollowupChoice>) queryChoice.list();
			FollowupResponse followupResponse = null;
			FollowupChoice followupChoice = recognizeChoice(followupChoices);
			if(followupChoice != null)
				followupResponse = setFollowUpResponse(followupChoice.getFcid(), fid);

			if(followupResponse != null){
				session.save(followupResponse);
				logger.info("Successfully saved response for followup "+fid);
				String recordSuccessMsg = getValueFromPropertyFile("recordSuccessMsg", "english"); 
				playUsingTTS(recordSuccessMsg,ttsNotation);	
			 }
			else{
				logger.info("Failed to get response for followup "+fid);
				String followupFailMsg = getValueFromPropertyFile("followupFailMsg", "english");
				playUsingTTS(followupFailMsg,ttsNotation);
			 }
		   }
		session.getTransaction().commit();
        }
        catch(Exception ex){
            ex.printStackTrace();
        } finally{
			if(session !=null)
				session.close();
		}
		
    }

	/**
	 * Identify what the choice made by the patient is.
	 * Initially, tries to understand the choice using ASR (with 2 retries)
	 * If the recognized option is one of the passed followupChoices, it is returned
	 * Otherwise, plays out the options as IVR menu and gets users choice.
	 * 
	 * @param followupChoices
	 * @return
	 */
	public FollowupChoice recognizeChoice(List<FollowupChoice> followupChoices){
	try{
		//get user response using ASR
        String choice = getOptionUsingAsr(2);
        logger.info("you said "+choice);
        for(FollowupChoice followupChoice : followupChoices){
        	if(choice.equals(followupChoice.getChoice().toLowerCase())){
        		return followupChoice;
        	}
        }
        
        //ASR failed. Get followup options using DTMF
		char option;
        int count = 1;
        String dtmfMenuText = "press";
        String dtmfDigits = "";
        for(FollowupChoice followupChoice : followupChoices){
            dtmfMenuText += " "+count+" for "+followupChoice.getChoice();
            dtmfDigits += ""+count;
            count++;
        }
        option=getOptionUsingTTS(dtmfMenuText,dtmfDigits,"5000",2);
		return followupChoices.get(Character.getNumericValue(option)-1);
	}
	catch(Exception ex){
		logger.error("Error getting followupchoice"+ex);
		return null;
		}
	}

	/**
   * Persists user response to followup questions
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

}