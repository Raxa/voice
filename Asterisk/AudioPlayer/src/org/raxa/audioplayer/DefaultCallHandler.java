
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
public class DefaultCallHandler extends CallHandler
{
	private AgiRequest request;
	AgiChannel channel;
	private Logger logger = Logger.getLogger(this.getClass());
	
	DefaultCallHandler(AgiRequest request, AgiChannel channel){
		super(channel,request);
		this.request=request;
	    this.channel=channel;
		try{
    		//service(request, channel);
			} catch(Exception ex){
				logger.error("\nCaused by:\n",ex);
			}	}
	

    
	  /**
	 * checks whether the call is incoming or outgoing.Handles the call accordingly
	 */
    public void service(AgiRequest request, AgiChannel channel) throws AgiException{
	}
}