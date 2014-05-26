


package org.raxa.alertmessage;
/**
 * 
 * Both scheduler and AudioPlayer use this class
 * This is the schema in which JSON is stored in column-ivrMsg:content
 *
 * @author Atul Agrawal
 *
 */
public class SMSContentFormat {

	/**
	 * Is the tag name which describe about what the content is about.
	 * <p>eg.Header1,Medicine,Dose
	 */
	private String field;
	/**
	 * content of the message
	 */
	private String content;
	/**
	 * mode in which the alert is to be played. eg.TTS,pre-recorded voice and whatever you have
	 */
	private int mode;
	
	public SMSContentFormat(String field,String content,int mode){
		this.field=field;
		this.content=content;
		this.mode=mode;
	}
	
	public SMSContentFormat(){}
	
	public String getField(){
		return field;
	}
	
	public String getContent(){
		return content;
	}
	
	public int getMode(){
		return mode;
	}
	

}

