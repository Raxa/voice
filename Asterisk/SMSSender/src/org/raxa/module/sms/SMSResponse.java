package org.raxa.module.sms;

public class SMSResponse {

	private String transactionID;
	private String status;
	private boolean isSuccess;
	
	
	public SMSResponse(String a,String b,boolean c){
		transactionID=a;
		status=b;
		isSuccess=c;
	}
	
	public SMSResponse(){
		transactionID=null;
		status="-1";
		isSuccess=false;
	}
	
	public String getTransID(){
		return transactionID;
		
	}
	
	public String getStatus(){
		return status;
	}
	
	public boolean getIsSuccess(){
		return isSuccess;
	}
	
	public void setTransId(String s){
		transactionID=s;
	}
	public void setStatus(String s){
		status=s;
	}
	public void setIsSuccess(boolean s){
		isSuccess=s;
	}
	
	public String toString(){
		String s="Response=> isSuccess:"+isSuccess+" transactionID:"+transactionID+" status:"+status;
		return s;
	}
	
}


