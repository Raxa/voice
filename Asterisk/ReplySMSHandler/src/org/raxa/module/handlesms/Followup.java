package org.raxa.module.handlesms;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.raxa.database.FollowupQstn;
import org.raxa.database.FollowupChoice;
import org.raxa.database.FollowupResponse;
import org.raxa.database.HibernateUtil;
import org.raxa.database.Patient;
import org.raxa.database.VariableSetter;
import org.raxa.registration.Register;

public class Followup extends Options implements VariableSetter {
	private static Logger logger = Logger.getLogger(Followup.class);
	private static MessageHandler mHandler = new MessageHandler();
	protected static Language defaultLanguage = Language.ENGLISH;
	private static String SUCCESS_REPLY = "Thanks, your response has been successfully saved.";
	public Followup(String userID, String pnumber) {
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
		
		return null;
	}

public static void handleFollowupResponse(String pnumber, String message,
			Date inDate) {
	//Get fid
	//Msg format: FWP <fid> <choice>
	//Msg format eg: FWP 10 2  
try {
		String [] split = message.split(" ");
		int fid = Integer.parseInt(split[1]);
		int choice = Integer.parseInt(split[2]);

		Patient patient = DatabaseService.getPatientForFollowup(pnumber, fid);
	    if(patient == null){
	    	mHandler.sendSMS(message, new Date(), pnumber, RMessage.INVALIDFOLLOWUPID.getMessage(), defaultLanguage);
	    	return;
	    }
	    List<FollowupChoice> followupChoices = DatabaseService.getFollowupChoices(fid);
	    if(choice > followupChoices.size()){
	    	mHandler.sendSMS(message, new Date(), pnumber, RMessage.INVALIDOPTION.getMessage(), defaultLanguage);
	    	return;
	    }
	    
	    FollowupResponse followupResponse = getFollowUpResponse(choice, fid);
	    if(DatabaseService.saveFollowupResponse(followupResponse)){
	    	mHandler.sendSMS(message, new Date(), pnumber, SUCCESS_REPLY, defaultLanguage);
	    } else{
	    	mHandler.sendSMS(message, new Date(), pnumber, RMessage.ERROR.getMessage(), defaultLanguage);
	    }
	    
		} catch (Exception ex){
			logger.info("Some error occured while fetching followupChoices");
			logger.error("\n ERROR Caused by\n",ex);
		}
	//Get followup Choices
	//check if choice is valid
	//save and send confirmation
	
	}

private static FollowupResponse getFollowUpResponse(int fcid, int fid){
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
