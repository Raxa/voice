package org.raxa.module.sms;

import java.util.List;

import org.apache.http.NameValuePair;
/**
 * This class should be extended by all gateways which are used to send SMS
 * @author atul
 *
 */
public abstract class  SMSHeaders {
		/**
		 * Base URL of the Gateway to send SMS
		 */
		public static String baseURL;
		/**
		 * get all URL PAramenters in form of NameValuePair
		 * @return URL Parameters
		 */
		public abstract List<NameValuePair> getHeader();
		
		/**
		 * Add a new Name Value Pairs
		 * @param value1
		 * @param value2
		 */
		public abstract void addNewValuePairs(String value1,String value2); 
		/**
		 * parse the information given by gateway on GET/POST request while sending an SMS and create a SMSResponse out of i
		 * @param result
		 * @return
		 */
		public abstract SMSResponse parseResponse(String result); 
			
}
