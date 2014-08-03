package org.raxa.scheduler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.io.PrintWriter;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.json.JSONArray;
import org.json.JSONObject;
import org.raxa.database.FollowupChoice;
import org.raxa.database.FollowupQstn;
import org.raxa.database.HibernateUtil;

import com.google.gson.Gson;


public class FollowupListener {
int port;
ServerSocket s;
final String DATE_FORMAT = "yyyy-MM-dd hh:mm:ss";
final String TIME_FORMAT = "HH:mm:ss";
SimpleDateFormat sdf;
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

            System.out.println(result);
 
            Gson gson = new Gson();
            sdf = new SimpleDateFormat(DATE_FORMAT);
            
            Session session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            
            //JSONObject json = gson.fromJson(result, JSONObject.class);
			JSONObject json = new JSONObject(result);
			System.out.println(json.toString());
            FollowupQstn followupQstn = new FollowupQstn();
            followupQstn.setFid(json.getInt("fid"));
            followupQstn.setFollowupType(json.getInt("followupType"));
            followupQstn.setPid(json.getString("pid"));
            followupQstn.setQstn(json.getString("qstn"));
            Date fromDate = sdf.parse(json.getString("fromDate"));
            Date toDate = sdf.parse(json.getString("toDate"));
			sdf.applyPattern(TIME_FORMAT);
            Date scheduleTime = sdf.parse(json.getString("scheduleTime"));
            followupQstn.setFromDate(new Timestamp(fromDate.getTime()));
            followupQstn.setToDate(new Timestamp(toDate.getTime()));
            followupQstn.setScheduleTime(new Time(scheduleTime.getTime()));
            
            JSONArray choices = json.getJSONArray("choices");
            FollowupChoice followupChoice;
            
            for(int i=0; i< choices.length(); i++){
				 followupChoice = new FollowupChoice();
            	 followupChoice.setFid(followupQstn.getFid());
				 followupChoice.setChoice(choices.getString(i));
	           	 session.save(followupChoice);
				logger.info("Successfully saved followup choice "+choices.getString(i));
            }
            
            session.save(followupQstn);
            logger.info("Successfully saved followup qstn "+followupQstn.getQstn());
			    
      //for binary data use
      // DataInputStream and DataOutputStream
 
      //for serialized objects use
      // ObjectInputStream and ObjectOutputStream
 	  PrintWriter printwriter = new PrintWriter(c.getOutputStream(),true);
      printwriter.println("success");
            
            session.getTransaction().commit();
            session.close();
	}
        catch(Exception e){
        	logger.error("Exception due to "+e);
        }
  }   
 }
}
	
