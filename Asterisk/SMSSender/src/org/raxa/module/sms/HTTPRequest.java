package org.raxa.module.sms;


import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.NameValuePair;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

public class HTTPRequest {

	/**
	 * HTTP POST
	 * @param args
	 */

	static String URLBase = null;
	static Logger logger=Logger.getLogger(HTTPRequest.class.getClass());
	/**
	 * Ass nameValuePairs to the URL and make a HTTP POST and return the response in form of a string
	 * @param headers
	 * @return response by the Gateway
	 */
	public static String getRequestPost(List <NameValuePair> headers){
		
		String result=null;
		HttpResponse response;
		DefaultHttpClient httpclient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(URLBase);
	//	HttpPost httpPost=new HttpPost("http://api.mVaayoo.com/mvaayooapi/MessageCompose?user=johnstoecker@gmail.com:fleebyteup&senderID=TEST SMS&receipientno=8093672342&dcs=0&msgtxt=This is Test message&state=4");
		try{
			httpPost.setEntity(new UrlEncodedFormEntity(headers));
		    response = httpclient.execute(httpPost);   
	        result=responseToString(response);
	    }
		catch(IOException ex){
			
			logger.error("IMPORTANT:Some error while sending message request");
			logger.error("\nCaused by:\n",ex);
			result=null;
		}
		finally{
			httpclient.getConnectionManager().shutdown();
		}

		return result;
		
	}
	/**
	 * 
	 * @param response
	 * @return String which contains response sent by SMS Gateway
	 */
	public static String  responseToString(HttpResponse response){
		
		String toReturn=null;
		try{
			BufferedReader rd = new BufferedReader(
	                new InputStreamReader(response.getEntity().getContent()));
			StringBuffer result = new StringBuffer();
			String line = "";int count=0;
			while ((line = rd.readLine()) != null) {
				result.append(line);
			}
			toReturn=result.toString();
		}
		catch(Exception ex){
			logger.error("\n ERROR while parsing output string \n Caused by\n",ex);
			toReturn=null;
		}
		
		return toReturn;
		
	}
	
	public static void setURLBase(String uRLBase) {
		URLBase = uRLBase;
	}
}


