/*  SimpleClient.java
*  @author Charles Bell
*  @version December 15, 2000
*  email: charles@quantumhyperspace.com
*/
 
import java.io.*;
import java.net.*;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
 
/** Sample client side code to send Followup question to server
 * The data is send as JSON
 * It is received and interpreted by FollowupListener class  
*/
public class SimpleClient{
 
  //String serverurl = "54.186.135.198";
	String serverurl = "127.0.0.1";
  int serverport = 12390;
  final String DATE_FORMAT = "yyyy-MM-dd hh:mm:ss";
  final String TIME_FORMAT = "HH:mm:ss";
  SimpleDateFormat sdf;
  static String fid;
  static final long ONE_MINUTE_IN_MILLIS=60000;//millisecs
 
  /**  Instantiates an instance of the SimpleClient class and initilaizes it.
  */
  public static void main(String[] args){
	  if(args.length >0)
		  fid = args[0];
	  else
		  fid = "10";
    SimpleClient simpleclient = new SimpleClient();
    simpleclient.init();
    
  }
 
  /**  Connects to the SimpleServer on port 8189 and sends a few demo lines
  *  to the server, and reads, displays the server reply,
  *  then issues a Bye command signaling the server to quit.
  */
  public void init(){
    Socket socket = null;    
    try{      
      Date date = new Date();
      long t=date.getTime();
      Date afterAddingTwoMins=new Date(t + (2 * ONE_MINUTE_IN_MILLIS));
      sdf = new SimpleDateFormat(TIME_FORMAT);
      String time = sdf.format(afterAddingTwoMins);
      
      sdf.applyPattern(DATE_FORMAT);
      String fromDate = sdf.format(new Date());

      String result = "{\"fid\":"+fid+","+
				"\"scheduleTime\":\""+time+
				"\","+
				"\"qstn\":\"What is your exercise level?\","+
				"\"followupType\":\"1\","+
				"\"choices\":"+
				"[\"high\","+
				"\"medium\","+
				"\"low\"],"+
				"\"toDate\":\""+fromDate+"\","+
				"\"pid\":\"1000abc\","+
				"\"fromDate\":\""+fromDate+"\""+
				"}";
      System.out.println(result);
      
      System.out.println("Connecting to " + serverurl + " on port " + serverport);
      socket = new Socket(serverurl,serverport);
      //Set socket timeout for 10000 milliseconds or 10 seconds just 
      //in case the host can't be reached
      socket.setSoTimeout(10000);
      System.out.println("Connected.");
      
      InputStreamReader inputstreamreader = new InputStreamReader(socket.getInputStream());
      BufferedReader bufferedreader = new BufferedReader(inputstreamreader);
      //establish an printwriter using the output streamof the socket object	
      //and set auto flush on    
      
      PrintWriter printwriter = new PrintWriter(socket.getOutputStream(),true);
      printwriter.println(result);
      String lineread = "";
      while ((lineread = bufferedreader.readLine()) != null){
        System.out.println("Received from Server: " + lineread);
      }
      System.out.println("Closing connection.");
      bufferedreader.close();
      inputstreamreader.close();
      printwriter.close();
      socket.close();
      System.exit(0);
 
    }catch(UnknownHostException unhe){
      System.out.println("UnknownHostException: " + unhe.getMessage());
    }catch(InterruptedIOException intioe){
      System.out.println("Timeout while attempting to establish socket connection.");
      intioe.printStackTrace();
    }catch(IOException ioe){
      System.out.println("IOException: " + ioe.getMessage());
    }finally{
      try{
        socket.close();
      }catch(IOException ioe){
        System.out.println("IOException: " + ioe.getMessage());
      }
    }
  }
}