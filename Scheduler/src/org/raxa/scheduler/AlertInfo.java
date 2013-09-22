package org.raxa.scheduler;
/**
 * This class contains information about which patient to call,what message to play and in what language
 * @author atul
 *
 */
public class AlertInfo {

		/**
		 * Patient phone number
		 */
		private String pnumber;
		/**
		 * patient prefered language
		 */
		private String preferLanguage;
		/**
		 * Patient message ID.
		 */
		private int msgId;
		/**
		 * Alert ID
		 */
		private String aid;  
		
		public AlertInfo(String pnumber,String preferLanguage,int msgId,String aid){
			this.pnumber=pnumber;
			this.preferLanguage=preferLanguage;
			this.msgId=msgId;
			this.aid=aid;
		}
		public String getPhoneNumber(){
			return pnumber;
		}
		
		public String getAlertId(){
			return aid;
		}
		
		public int getMsgId(){
			return msgId;
		}
		
		public String getpreferLanguage(){
			return preferLanguage;
		}
}


