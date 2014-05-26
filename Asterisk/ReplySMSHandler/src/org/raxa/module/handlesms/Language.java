package org.raxa.module.handlesms;
/**
 * Contains language and its transaltion Notation
 * @author atul
 *
 */
public enum Language {
	ENGLISH("English","en"),
	HINDI("Hindi","hi");
	/**
	 * Translation notation
	 * <p>eg.googletts maps english to en
	 */
	private final String translateNotation;
	/**
	 * name of the language as stored in database
	 */
	private final String langaugeName;
	
	Language(String language,String notation){
		langaugeName=language;
		translateNotation=notation;
	}
	
	public String getLanguage(){
		return langaugeName;
	}
	
	public String getNotation(){
		return translateNotation;
	}

}
