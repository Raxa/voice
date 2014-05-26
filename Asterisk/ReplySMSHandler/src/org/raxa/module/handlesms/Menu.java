package org.raxa.module.handlesms;


import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class Menu extends Options {
	/**
	 * maps option to keyword
	 * eg 1 -> Register
	 * eg 2->  Rem
	 */
	private static Map<Integer,Keyword> digitToKeyWord;
	

	public Menu(String userID, String pnumber) {
		super(userID, pnumber);
	}

	public Menu(int state, boolean deleteSession, String userID, String pnumber) {
		super(state, deleteSession, userID, pnumber);
	}

	public Menu(int state, boolean deleteSession, String pnumber) {
		super(state, deleteSession, pnumber);
	}

	@Override
	public String getKeyWord() {
		
		return Keyword.MENU.getKeyword();
	}

	@Override
	public String getDescription() {
		
		return Keyword.MENU.getDescription();
	}
	
	/**
	 * eg. 1. Get your Medicine Reminder
	 * 		2.Register for the service
	 * 
	 * @return a string of list of options and there description
	 */
	private String getAllOptions(){
		String menustring;int count=1;
		if(Menu.digitToKeyWord==null){
			Menu.digitToKeyWord=new HashMap<Integer,Keyword>();
			mapInttoKeyWord(Menu.digitToKeyWord);
		}
		menustring="Main Menu :\n";
		Iterator<Map.Entry<Integer,Keyword>> itr=Menu.digitToKeyWord.entrySet().iterator();
		while(itr.hasNext()){
			Entry<Integer,Keyword> entry=itr.next();
			menustring+=entry.getKey()+". "+entry.getValue().getDescription()+"\n";
		}
		return menustring;
	}

	/**
	 * populate a map with option and keyword
	 * 
	 * @param digitToKeyWord2
	 */
	private void mapInttoKeyWord(Map<Integer, Keyword> digitToKeyWord2) {
		int count=1;
		for(Keyword k:Keyword.values()){
			if(!(k.getKeyword().equals(this.getKeyWord()))){
				digitToKeyWord2.put(count,k);
				++count;
			}
		}
	}
	
	
	/**
	 * send a reply based on the state a patient is in.
	 * 
	 */
	@Override
	public String getReply(String message, String pnumber) {
		String reply=null;
		if(state==0){
			state=1;
			reply=getAllOptions();
		}
		else if(state==1){
			Integer option=Integer.valueOf(MessageHandler.getMessageOption(message));
			if(Menu.digitToKeyWord.containsKey(option)){
				Keyword k=Menu.digitToKeyWord.get(option);
				Options query=IncomingSMS.getOptionFromClass(pnumber, getUserID(), k.getOptionClass());
				if(query==null){
					deleteSession=true;
					reply="Sorry some Error Occured";
				}
				else{
					query.setState(-1);							//Sets state of the chosed Option to -1.
					reply=query.getReply(message, pnumber);
					IncomingSMS.redirect(this,query);
				}
			}
		
		}
		if(reply==null)
			return reply;
		else
			return reply+"\n"+getLastLine();
		
	}
	/**
	 * 
	 * @param message containing user option
	 * @return patient chosen option
	 * @return 0 if any invalid option is chosen
	 */
	private int getMessageOption(String message) {
		String[] split=message.split(" ");
		int option=0;
		try{
			option=Integer.parseInt(split[1]);
		}
		catch(Exception ex){}
		return option;
	}
	
	

}
