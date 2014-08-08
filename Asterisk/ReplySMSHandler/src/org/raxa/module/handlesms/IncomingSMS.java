package org.raxa.module.handlesms;

import java.io.IOException;

import javax.servlet.ServletException;
//import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.raxa.database.FollowupChoice;
import org.raxa.database.HibernateUtil;

import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.Cache;

/**
 * Servlet implementation class IncomingSMS
 */
//@WebServlet("/incomingsms")
public class IncomingSMS extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static String FOLLOWUP_KEYWORD = "FWP";
	private static Logger logger = Logger.getLogger(IncomingSMS.class);
	/**
	 * Stores cache of Patient ID and Object(extends Options) associated with it
	 */
	private static Cache<String, Options> cache; 
	/**
	 * Maps keyword to its class
	 * <p>eg.Register -> org.raxa.module.handlesms.Registration Class, so that object of the class can be created from keyword using Reflection
	 * 
	 */
	private static Map<String,Class> map;
	/**
	 * Max Size of cache
	 */
	private static int maxSize;
	/**
	 * Characters that will be present in ID Generated
	 */
	private static char alphas[];
	/**
	 * total length of alphas[]
	 */
	private static int alphasLength;
	/**
	 * Keyword to start a query
	 */
	protected final static String hello="GET";
	/**
	 * Since the ID generated are 3 letter alphabets the ID generated can't be 'GET' as the application will confuse
	 * wether its for new Query or already generated query.
	 * <p>4856 maps to GET.Look at getString(int) for more info <p>
	 * 
	 */
	private final static int helloStringID=4856;
	/**
	 * It stores a set of Generated ID which hasn't yet put in the cache but has been assigned to an Options
	 * 
	 */
	private static Set<String> uncachedID;
	/**
	 * Default Language to send SMS
	 */
	protected static Language defaultLanguage;
	/**
	 * Maps a String to Lnaguage
	 * <p>eg. Maps english -> Language.English
	 */
	public static Map<String,Language> languageMap;
	/**
	 * Cache timeout in minutes
	 */
	private static final int cacheTimeout=120;	
//	public static PrintWriter out;   //To be deleted.For testing purpose
	
	
	public IncomingSMS() {
        super();
        
    }
    
    public void init() throws ServletException {
    	
    	 alphas ="ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
         alphasLength=alphas.length;
         maxSize=alphasLength*alphasLength*alphasLength;
         map=new HashMap<String,Class>();
         uncachedID=new HashSet<String>();
         putObjectInMap();
         defaultLanguage=(Language.ENGLISH);
         cache=CacheBuilder.newBuilder().
				maximumSize(maxSize).
				expireAfterAccess(cacheTimeout, TimeUnit.MINUTES).
				build();
         languageMap=new HashMap<String,Language>();
         for(Language l:Language.values()){
        	 languageMap.put(l.getLanguage().toUpperCase(),l);
         }
    }
    
	/**
	 * 
	 * 
	 * @URL http://userip:port/urlpattern?userid=(userid)&oa=(replynumber)&da=(receipientno)&dtime=(datetime)&msgtxt=(message)
 
	 *  <p>Following Input is Expected from Patient</p>
	 *<p>
	 *<GET> <KEYWORD> <EXTENSION> eg. GET REMINDER TOMORROW or GET REMINDER 4PM or GET REMINDER</p>
	 *OR
	 *<p>
	 *<userID> <DIGIT>     eg. PQE 1 or PQE yes
	 *</p>
	 * 
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 * 
	 */
    
   /**
    * <p>Get all the variables Send by SMS Gateway
    * <p>Check if the variables are valid
    * <p>If not,make status=1 and send that
    * <p>Checks if the message is valid
    * <p>Checks if the query is a new query or and continued query
    * <p>Send the request to be handle by handleGET or handleID
    */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//this.out=response.getWriter();
		PrintWriter out=response.getWriter();
		
		String message=request.getParameter("msgtxt");String pnumber=request.getParameter("da");String time=request.getParameter("dtime");
		String fromNumber=request.getParameter("oa");String userId=request.getParameter("userid");
		Date inDate=getTimeFromString(time);Date outDate=new Date();
		
		if(pnumber==null||pnumber=="" || pnumber==" " || inDate==null){
			out.println("Status=1");
			return;
		}
		
		DatabaseService.saveIncomingSMS(pnumber, fromNumber, inDate, message);
		
		MessageHandler mHandler=new MessageHandler(message);
		IMessage messageType=mHandler.getMessageFormat();
		
		if(!(messageType.getToContinue())){
			mHandler.sendSMS(message,outDate, pnumber,mHandler.getErrorMessage(messageType),defaultLanguage);
		}
		
		if(messageType==IMessage.GET)
			handleGet(mHandler,pnumber,message, inDate);
		
		else if(messageType==IMessage.ID)
			handleID(mHandler,pnumber,message, outDate);
		
		
		out.println("Status=0");
	}
	
	/**
	 * Handles message whose first word is an ID.
	 * <p>Check if message is right format.</p>
	 * <p>Send an SMS to the query</p>
	 * <p>Checks if the query is answered</p>
	 * <p>Delete from the cache if the query is answered</p>
	 * 
	 * @param mHandler  Object which do stuffs related to message
	 * @param pnumber	Patient phone number
	 * @param message	Patient query
	 * @param inDate	Time at which message received
	 */
	private void handleID(MessageHandler mHandler, String pnumber,String message,Date inDate) {
		String[] split=message.split(" ");String reply;
		String userID=split[0].toUpperCase();
		//The keyword for followup is fixed as followup qstn is being send by a different process. 
		//In case of further features with fixed ids, could consider moving the ids to a enum
		if(userID.equals(FOLLOWUP_KEYWORD)){
			Followup.handleFollowupResponse(pnumber, message, inDate);
			return;
		}
		Options option=cache.getIfPresent(userID);
		if(option==null){
			mHandler.sendSMS(message,inDate,pnumber, mHandler.getErrorMessage(RMessage.INVALIDSESSION),defaultLanguage);
			return;
		}
		else if(!(pnumber.equals(option.getPhoneNumber()))){
			mHandler.sendSMS(message,inDate, pnumber, mHandler.getErrorMessage(RMessage.DIFFERNETSESSIONID),defaultLanguage);
			return;
		}
		
		reply=option.getReply(message, pnumber);
		
		if(reply==null && !(option.toDeleteSession()))
				mHandler.sendSMS(message,inDate, pnumber, mHandler.getErrorMessage(RMessage.INVALIDOPTION),option.getLanguage());
		
		else if(reply==null)
			mHandler.sendSMS(message,inDate, pnumber, mHandler.getErrorMessage(RMessage.NOTHINGTOREPLY),option.getLanguage());
		
		else
			mHandler.sendSMS(message,inDate, pnumber,reply,option.getLanguage());
		
		Options updatedOption=cache.getIfPresent(userID);
		
		if(updatedOption.toDeleteSession())
			cache.invalidate(userID);
		
					
		//Cache update itself automatically.		 
	}
	

	/**
	 * <p>Take a get request
	 * <p>Identify the keyword
	 * <p>make a new class from the  map and create an object.Generate and ID and assign the object that ID
	 * <p> Send back an reply and cache the object if necessary
	 * @param mHandler  Object which do stuffs related to message
	 * @param pnumber	Patient phone number
	 * @param message	Patient query
	 * @param inDate	Time at which message received
	 */
	private void handleGet(MessageHandler mHandler, String pnumber,String message,Date inDate){
		Options option;String userID;String reply;
		String[] split=message.split(" ");
		String keyword=split[1].toUpperCase();
		
		if(!map.containsKey(keyword)){
			mHandler.sendSMS(message,inDate, pnumber, mHandler.getErrorMessage(RMessage.KEYWORDNOTFOUND),defaultLanguage);
			return;
		}
			
		Class classname=map.get(keyword);
		userID=getUniqueUserID();
		option=getOptionFromClass(pnumber,userID,classname);
		if(option==null)
			return;
		if(!verifyNumber(pnumber,option)){
			mHandler.sendSMS(message,inDate, pnumber, mHandler.getErrorMessage(RMessage.NUMBERNOTRECOGNISED),defaultLanguage);
			return;
		}
		
		putInCache(userID,option);
		reply=option.getReply(message, pnumber);
		
		if(reply==null && !(option.toDeleteSession())){
				mHandler.sendSMS(message,inDate, pnumber, mHandler.getErrorMessage(RMessage.INVALIDOPTION),option.getLanguage());
				return;
		}
		
		else if(reply==null){
			mHandler.sendSMS(message,inDate, pnumber, mHandler.getErrorMessage(RMessage.NOTHINGTOREPLY),option.getLanguage());
			return;
		}
		else
			mHandler.sendSMS(message,inDate, pnumber,reply,option.getLanguage());		
	}
	/**
	 * put element in the cache
	 * @param userID ID Generated
	 * @param Options Object associated with id
	 * 
	 */
	
	private void putInCache(String userID,Options option){
		cache.put(userID, option);
		if(uncachedID.contains(userID))
			uncachedID.remove(userID);
	}
	/**
	 * Not Implemented:May require in future of security concerns.Or avoiding spam.Mvayoo does that for the users
	 * <p>If some smsGateway doesn't than we need to verify the number
	 * @param pnumber
	 * @param option
	 * @return
	 */
	private boolean verifyNumber(String pnumber, Options option) {
				
		return true;
	}

	/**
	 * 
	 * All post requests are handle by get request
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request,response);	
	}
	
	/**
	 * Returns a iterator of map of entrySet.
	 * @return Iterator<Map.Entry<String,Options> >
	 */
	public final static Iterator<Map.Entry<String,Class>> getMapIterator(){
		return map.entrySet().iterator();
	}
	/**
	 * It jumps or redirect from one menu option to other by changing field in cache.
	 * @param sessionID
	 * @param option
	 */
	public final static void redirect(Options me,Options redirect){
			cache.put(me.getUserID(), redirect);
	}
	
	/**
	 * Fills the map datamemebr.
	 * <p>for all keywords maps keywords with their associated class
	 */
	private void putObjectInMap(){
		for(Keyword k:Keyword.values()){
			String keyword=k.getKeyword();
			Class classname=k.getOptionClass();
			if(classname!=null && keyword!=null){
				map.put(keyword,classname);
			}
			else
				printE("IMPORTANT:Keyword:"+keyword+" not supported as Class doesnot exist");
		}
	}
	/**
	 * Return a unique ID which is not in the cache or uncachedID
	 * @return new ID Generated
	 */
	private String getUniqueUserID(){
		
		if(cache.size()==maxSize)
			return null;
		String id;
		while(true){
			int random=randInt(1,alphasLength*alphasLength*alphasLength);
			if(random!=helloStringID){
				id=getString(random);
				if(!uncachedID.contains(id) && cache.getIfPresent(id)==null && id !=FOLLOWUP_KEYWORD)
					break;
			}
		}
	//	out.println("ID Generated:"+id);
		uncachedID.add(id);
		return id;
	}
	/**
	 * Helper function to generate ID
	 * Maps a random number to a string
	 * @param random
	 * @return 
	 */
	private static String getString(int random){
		
		char thirdChar,secondChar,firstChar;int third,second,first;
		third=random%alphasLength;
		if(third==0)
			third=alphasLength;
		random=random-third;
		
		int quotient=random/alphasLength;
		second=(quotient%alphasLength)+1;
		first=((quotient-(second-1))/alphasLength)+1;
		
		thirdChar=alphas[third-1];secondChar=alphas[second-1];firstChar=alphas[first-1];
		
		char[] charId={firstChar,secondChar,thirdChar}; 
		String id=new String(charId);
		
		return id;
	}
	
	/**
	 * generate a random integer
	 * @param min
	 * @param max
	 * @return
	 */
	protected final static int randInt(int min, int max) {
		Random rand = new Random();
		int randomNum = rand.nextInt((max - min) + 1) + min;
	    return randomNum;
	}
	
	
	protected static void print(String s){
		logger.info(s);
	}
	protected static void printE(String s){
		logger.error(s);
	}
	
	/**
	 * Create an Options class from a given Parameter
	 * @param pnumber patient Number
	 * @param userID  ID Generated
	 * @param classObject Class 
	 * @return
	 */
	protected final static Options getOptionFromClass(String pnumber,String userID,Class classObject){
		try{
		return (Options)classObject.getConstructor(String.class,String.class).newInstance(userID,pnumber);
		}
		catch(Exception ex){
			logger.error("\n Error while tryin to create Object from class \n Caused by:\n",ex);
			return null;
		}
	}
	
	/**
	 * eg.Convert string "2013-03-03 12:12:12" to date
	 * @param time(in date format)
	 * @return Date
	 */
	private Date getTimeFromString(String time){
		Date temp;
		try{
			temp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(time);
		}
		catch(Exception ex){
			logger.error("\n ERROR Caused by\n",ex);
			temp=null;
		}
		return temp;
	}
	
	
}
