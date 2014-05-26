package org.raxa.module.handlesms;
/**
 * <p>CAUTION:Following Things should be Kept in Mind while creating any option
 * <p>1.The class itself has to handle changing of state.So before returning from the class ensure that you have changed your state accordingly so that the
 * next user query will go to that state directly.
 * <p>2.If the class has answered the user query and does not expect a reply from user be sure to make deleteSession to true such that no further query from that session will be entertain
 * 
 * 
 * <p>If the option entered by the user is not expected return null and set deleteSession to false
 * 
 * <p>If there is nothing to send return null.
 * 
 * <p>If state=-1 that means it is redirected from main menu.
 * 
 * @author Atul Agrawal
 *
 */
public abstract class Options {
		/**
		 * Current state of the conversation
		 */
		protected int state=0;
		/**
		 * Wether the application has answered the user query or is not expection any reply in the conversation
		 */
		protected boolean deleteSession=false;
		/**
		 * ID generated for the object
		 */
		private final String userID;
		/**
		 * user prefer langauge
		 */
		protected Language language=Language.ENGLISH;
		/**
		 * user phone number
		 */
		private final String pnumber;
		/**
		 * patient id
		 */
		protected String pid=null;
		/**
		 * patient name
		 */
		protected String pname;
		/**
		 * whether the class need to verify patient number.
		 */
		private boolean ifVerifyNumber=false;
		/**
		 * KeyWord associated with the option.
		 * ex. REM for medicine Reminder
		 * 
		 * @return
		 */
		public abstract String getKeyWord();
		
		/**
		 * Gets the description of the option which will be visible in main-menu.
		 * 1.Medicine Reminder
		 * @return
		 */
		protected void setState(int i){
			state=i;
		}
		/**
		 * 
		 * @return if to verify patient number
		 */
		protected boolean ifVerifyNumber(){
			return ifVerifyNumber;
		}
		/**
		 * 
		 * @return whether to delete session
		 */
		protected boolean toDeleteSession(){
			return deleteSession;
		}
		/**
		 * 
		 * @param set if patient number needs to be verified
		 */
		protected void setNumberVerification(boolean b){
			ifVerifyNumber=true;
		}
		
		/**
		 * 
		 * @return get description of the class
		 */
		protected abstract String getDescription();
		
		/**
		 * 
		 * @return patient ID
		 */
		protected String getPid(){
			return pid;
		}
		
		/**
		 * 
		 * @param set patient ID
		 */
		protected void setPid(String s){
			pid=s;
		}
		/**
		 * 
		 * @return get state of the conversation
		 */
		protected int getState(){
			return state;
		}
		/**
		 * 
		 * @param set patient name
		 */
		protected void setPname(String s){
			this.pname=s;
		}
		/**
		 * 
		 * @return get paitent name
		 */
		protected String getPname(){
			return pname;
		}
		/**
		 * USE:Can be used to advertise or to add  any notification
		 * @return get last line to be sent with the message
		 */
		protected String getLastLine(){
			return "Type "+userID+" (space)"+"(your option)"+"to send your option.";
		}
		/**
		 * 
		 * @return language of the paitent
		 */
		public Language getLanguage(){
			return language;
		}
		
		/**
		 * 
		 * @param set language of the patient
		 */
		
		public void setLanguage(Language l){
			this.language=l;
		}
		/**
		 * 
		 * @return get phone number of patient
		 */
		public String getPhoneNumber(){
			return pnumber;
		}
		/**
		 * 
		 * @param mesasge
		 * @param pnumber
		 * @return get reply for a query sent by user
		 */
		public abstract String getReply(String message,String pnumber);
		
		/**
		 * 
		 * @return get user ID
		 */
				
		public String getUserID(){
			return userID;
		}
		
		public Options(int state,boolean deleteSession,String userID,String pnumber){
			this.state=state;
			this.deleteSession=deleteSession;
			this.userID=userID;
			this.pnumber=pnumber;
		}
		
		public Options(int state,boolean deleteSession,String pnumber){
			this(state,deleteSession,null,pnumber);
		}
		/**
		 * First step(accessed via keyword or through main menu) i.e head is 0;
		 */
		public Options(String userID,String pnumber){
			this(0,false,userID,pnumber);
		}
		
}
