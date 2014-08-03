package org.raxa.module.sms;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.log4j.Logger;


/**
 * This class is visible to outer world
 * @author atul
 *
 */
public class SMSSender {
	
	private boolean isAuthorised=false;
	private SMSHeaders headers;

	/**
	 * This method is called to send an sms
	 * @param message message to be sent
	 * @param pnumber number to send the sms
	 * @param senderID senderID who is sending the sms
	 * @param preferLanguage language in which sms should be snet
	 * @return
	 */
	public SMSResponse sendSMSThroughGateway(String message,String pnumber,String senderID,String preferLanguage){
		
		
		Logger logger=Logger.getLogger(this.getClass());SMSResponse smsresponse;
		if(!isAuthorised)
			return null;
		try{
			
			String result=HTTPRequest.getRequestPost(getHeadersAndSetBaseUrl(message,pnumber,senderID));
			if(result==null)
				throw new Exception();
			System.out.println("result");
			logger.error(result);
			smsresponse=parseResult(result);
			logger.debug(smsresponse.toString());
			
		}
		catch(Exception ex){
			
			logger.error("Some Error occured while sending SMS to:"+pnumber+"from senderId:"+senderID);
			logger.error("\nCaused by:\n",ex);
			smsresponse=null;
		}
		return smsresponse;
	}
	/**
	 * Get header and base URl 
	 * NEEDS To be changed if Gateways are changed
	 * @param message
	 * @param pnumber
	 * @param senderId
	 * @return
	 */
	private List <NameValuePair> getHeadersAndSetBaseUrl(String message,String pnumber,String senderId){
		headers=new mvayooSMSHeader(message,pnumber,senderId);
		
		HTTPRequest.setURLBase(headers.baseURL);
		
		return headers.getHeader();
	}
	
	private SMSResponse parseResult(String result){
		return headers.parseResponse(result);
	}
	/**
	 * Right now it authorises anybody.later we can keep a file or maintain a database to check for username and password.
	 * As we cannot share the gateway SMS username and password it needs a way to block random access.
	 * This login credentials will be given to hospital Authority.
	 * @param username
	 * @param password
	 * @return
	 */
	public boolean login(String username,String password){
		//do something with username and password.
		isAuthorised=true;
		return isAuthorised;
	}
	
	public static void main(String[] args)
	{
		SMSSender smsSender = new SMSSender();
		smsSender.sendSMSThroughGateway("Main Menu : 1. Get your Medicine Reminder 2. Register yourself for the service 3. Unregister yourself from the service  Type THG (space)(your option)to send your option.",
				"9160741100", "TEST SMS", "english");
		
	}
	
	
}
