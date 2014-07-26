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
public class InteractionCallHandler extends CallHandler
{
	private AgiRequest request;
	AgiChannel channel;
	String pid;
	
	private Logger logger = Logger.getLogger(this.getClass());
	
	InteractionCallHandler(AgiRequest request, AgiChannel channel, String language, String pid){
		super(channel,request,language);
		this.request=request;
	    this.channel=channel;
		try{
    		service(request, channel);
			} catch(Exception ex){
				logger.error("\nCaused by:\n",ex);
			}	}
	

    
	  /**
	 * checks whether the call is incoming or outgoing.Handles the call accordingly
	 */
    public void service(AgiRequest request, AgiChannel channel) throws AgiException{
	playInteractionMenu();
	}
	
	
public void playInteractionMenu()
{
	//TODO: Get pid from pnumber
	pid = "111";
	String doctorInteractionText = getValueFromPropertyFile("doctorInteractionText","english");
	try {
		char option=getOptionUsingTTS(doctorInteractionText,"1234569","5000",2);
		String keyWord=analyseOption("doctorInteractionMenu",option);
		if(keyWord.equals("message"))
		{
			recordVoiceMessage();
		}
		else if(keyWord.equals("playback"))
		{
			playbackRecordedCall();			
		}
		else if(keyWord.equals("call"))
		{
			callDoctor();
		}
		else if(keyWord.equals("mainmenu"))
			return;
	} catch (AgiException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
}

private void callDoctor() {
//TODO: Get doctorNumber from Raxa based on pid
String doctorNumber = "SIP/doctor";
try{
	playUsingTTS("Calling your doctor ",ttsNotation);
	channel.exec("DIAL","SIP/billy,100,,");	
}
catch(Exception e){
	e.printStackTrace();
}
}

//TODO: Move strings to config
private void playbackRecordedCall() {
	String audioPath = "/var/lib/asterisk/sounds/"; 
	try {
		if(new File(audioPath+"recording"+pid+".wav").exists()){
	        playUsingTTS("Playing your recorded message ",ttsNotation);
			channel.streamFile(audioPath+"recording"+pid);	
		}
		else
		{
			playUsingTTS("You do not have a recorded message ",ttsNotation);
		}
	} catch (AgiException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
}

private void recordVoiceMessage() {
	String audioPath = "/var/lib/asterisk/sounds/";
	try {
	playUsingTTS("Please record a voice message after the beep ",ttsNotation);
		channel.exec("Record","recording"+pid+":wav,4,20,");
	playUsingTTS("Your message has been recorded ",ttsNotation);
	} catch (AgiException ae) {
		ae.printStackTrace();
		try{
			playUsingTTS("Sorry unable to record your message",ttsNotation);
		}catch(Exception e){
			e.printStackTrace();
		}
	}	
}


}