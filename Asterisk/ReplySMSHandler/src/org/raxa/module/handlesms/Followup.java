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

/**
 * This class extends Options and handles incoming followup SMS
 * Followup SMS is initiated by the Scheduler whereas other sms handlers are initiated by the patient
 * Hence it is not possible to create a unique ID for the transaction in the incomingSMS cache
 * the followup response sms handler goes through a different workflow (handleFollowupResponse)
 * 
 * @author rahulr92
 *
 */
public class Followup extends Options implements VariableSetter {
	private static Logger logger = Logger.getLogger(Followup.class);
	private static MessageHandler mHandler = new MessageHandler();
	protected static Language defaultLanguage = Language.ENGLISH;
	private static String SUCCESS_REPLY = "Thanks, your response has been successfully saved.";
	public Followup(String userID, String pnumber) {
		super(userID, pnumber);
	
	}

	/**
	 * keyword isn't required since followup SMS is not initiated by the patient
	 * It is always initiated by Scheduler
	 */
	@Override
	public String getKeyWord() {	
		return null;
	}

	/**
	 * description also not required as followup is not part of any menu
	 */
	@Override
	protected String getDescription() {
		return null;
	}
	
	/**
	 * Reply is not generated using standard workflow. Hence getReply doesn't need to be defined.
	 */
	@Override
	public String getReply(String message, String pnumber) {
		return null;
	}

/**
 * handles response of followup question sent via Scheduler
 * Msg format: FWP <fid> <choice>
 * Msg format eg: FWP 10 2  
 * @param pnumber
 * @param message
 * @param inDate
 */
public static void handleFollowupResponse(String pnumber, String message,
			Date inDate) {
	//Get fid
	try {
		String [] split = message.split(" ");
		int fid = Integer.parseInt(split[1]);
		int choice = Integer.parseInt(split[2]);

		Patient patient = DatabaseService.getPatientForFollowup(pnumber, fid);
		//In case the patient who replied is not part of the followup qstn
	    if(patient == null){
	    	mHandler.sendSMS(message, new Date(), pnumber, RMessage.INVALIDFOLLOWUPID.getMessage(), defaultLanguage);
	    	return;
	    }
	    //check whether the patients choice is valid
	    List<FollowupChoice> followupChoices = DatabaseService.getFollowupChoices(fid);
	    if(choice > followupChoices.size()){
	    	mHandler.sendSMS(message, new Date(), pnumber, RMessage.INVALIDOPTION.getMessage(), defaultLanguage);
	    	return;
	    }
	    //persist the response to db and send confirmation
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
	
	}

/**
 * returns a followup response instance with the specified fcid and fid
 * @param fcid
 * @param fid
 * @return
 */
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
