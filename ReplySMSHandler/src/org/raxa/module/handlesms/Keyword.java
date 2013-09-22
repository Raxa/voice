package org.raxa.module.handlesms;

/**
 * Contains Keyword which user will send like
 * <p>GET MENU
 * <p>GET REM
 * <p>GET UNREG
 * 
 *<p>Enum shouldnot be change while runtime.Will break Menu.class
 * @author atul
 *
 */
public enum Keyword {
	REMINDER("REM","org.raxa.module.handlesms.Reminder","Get your Medicine Reminder"),
	MENU("MENU","org.raxa.module.handlesms.Menu","Get Menu"),
	REGISTER("REG","org.raxa.module.handlesms.Registration","Register yourself for the service"),
	UNREGISTER("UNREG","org.raxa.module.handlesms.UnRegister","Unregister yourself from the service");
	/**
	 * Keyword which user will send
	 */
	private final String keyword;
	/**
	 * Class name associated with that keyword
	 */
	private final String classname;
	/**
	 * Description of the class.What the class does.This will be used in main menu to explain options
	 */
	private final String description;
	Keyword(String keyword,String classname,String description){
		this.keyword=keyword;
		this.classname=classname;
		this.description=description;
	}
	
	public Class getOptionClass(){
		try{
			return Class.forName(classname);
		}
		catch(Exception ex){
			
			return null;
		}
	}
	
	public String getKeyword(){
		return keyword;
	}
	
	public String getDescription(){
		return description;
	}
}
