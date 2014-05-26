package org.raxa.alertmessage;
/**
 * This is the schema in which JSON is stored in column-ivrMsg:content
 * @author atul
 *
 */
public class ContentFormatSMS {
	/**
	 * is the tag name which describe about what the content is about.
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
	
	public ContentFormatSMS(String field,String content,int mode){
		this.field=field;
		this.content=content;
		this.mode=mode;
	}
	
	public ContentFormatSMS(){}
	
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
