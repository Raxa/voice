package org.raxa.scheduler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.io.PrintWriter;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.raxa.database.FollowupChoice;
import org.raxa.database.FollowupQstn;
import org.raxa.database.HibernateUtil;

import com.google.gson.Gson;

/**
 * This class creates a followup listener that listens to 
 * a predefined PORT for new followup questions from Raxa server
 * Received qstns are persisted to db so that Scheduler can pick them up
 * See below for example JSON message from Raxa server
 * @author rahulr92
 *
 */
public class FollowupListener {
final int PORT = 12390;
ServerSocket s;
final String DATE_FORMAT = "yyyy-MM-dd hh:mm:ss";
final String TIME_FORMAT = "HH:mm:ss";
SimpleDateFormat sdf;
 static Logger logger = Logger.getLogger(FollowupListener.class); 

	public void  start(){
	   try { 
		   s = new ServerSocket(PORT);
		   logger.info("listening to port "+PORT);
	   	 } 
	    catch (IOException e) {
	        System.out.println("Could not listen on port: " + PORT);
	        System.exit(-1);
	    }
    Socket c = null;
    while (true) {
        try {
            c = s.accept();
        } catch (IOException e) {
            System.out.println("Accept failed: " + PORT);
            System.exit(-1);
        }
    try {
        BufferedReader in = new BufferedReader(new InputStreamReader(c.getInputStream()));
        String inputLine = null;
        String result = "";
        while ((inputLine = in.readLine()) != null) {
            result = result.concat(inputLine);   
        }
        logger.info(result);
        saveFollowup(result);            
        PrintWriter printwriter = new PrintWriter(c.getOutputStream(),true);
        printwriter.println("success");
		} catch(Exception e){
        	logger.error("Exception due to "+e);
        }
    }   
 }

	/**
	 * @param result
	 * @throws HibernateException
	 * @throws JSONException
	 * @throws ParseException
	 */
	private void saveFollowup(String result) throws HibernateException,
			JSONException, ParseException {
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
		
		session.getTransaction().commit();
		session.close();
	}
}
/*
 {
"fid":5,
"scheduleTime":"21:11:09",
"qstn":"What is your exercise level?",
"followupType":
"choices":
	["high",
	"medium",
	"low"],
"toDate":"2014-06-21 21:11:09",
"pid":10,
"fromDate":"2014-06-21 21:11:09"
}
*/

	
