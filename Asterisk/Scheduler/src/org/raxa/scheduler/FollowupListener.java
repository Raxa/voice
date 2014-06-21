package org.raxa.scheduler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class FollowupListener {
int port;
ServerSocket s;
 static Logger logger = Logger.getLogger(FollowupListener.class); 

	public void  start(){
	   try {
	       port = 12390; 
		   s = new ServerSocket(port);
		   logger.info("listening to port "+port);
	    } 
	    catch (IOException e) {
	        System.out.println("Could not listen on port: " + port);
	        System.exit(-1);
	    }
    Socket c = null;
    while (true) {
        try {
            c = s.accept();
        } catch (IOException e) {
            System.out.println("Accept failed: " + port);
            System.exit(-1);
        }
        try {

            BufferedReader in = new BufferedReader(new InputStreamReader(c.getInputStream()));
                String inputLine = null;
            String result = "";
            while ((inputLine = in.readLine()) != null) {
                result = result.concat(inputLine);  
            }
            //System.out.println(result);
            Gson gson = new GsonBuilder().setDateFormat("dd-M-yyyy hh:mm:ss").create();
            FollowupData followupData = gson.fromJson(result, FollowupData.class);
            
	}
        catch(Exception e){
        	logger.error("Exception due to "+e);
        }
  }   
 }
}
	
