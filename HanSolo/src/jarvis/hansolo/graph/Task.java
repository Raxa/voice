/*
 *
 * See the file "LICENSE" for information on usage and
 * redistribution of this file, and for a DISCLAIMER OF ALL
 * WARRANTIES.
 * 
 * Raxa.org
 *
 */

package jarvis.hansolo.graph;

import jarvis.hansolo.utils.tools.Variable;
import jarvis.hansolo.utils.tools.VariableType;
import jarvis.leia.message.Message;
import jarvis.leia.stream.MessageHandler;
import jarvis.leia.stream.Publisher;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Observable;
import java.util.Observer;
import java.util.StringTokenizer;

public class Task {
	
	private TaskType TYPE;
	private HashMap<Integer, Variable> varMap;
	private String INFO;
	private int varID;			// What variable to perform the task on
	private MessageHandler msgHandle;
	public Task(TaskType type, int varID) {
		this.TYPE = type;
		this.varID = varID;
	}
	
	public void setVarMap(HashMap<Integer, Variable> varMap){
		this.varMap = varMap;
	}
	
	public void setMessageHandler(MessageHandler msgHandler) {
		this.msgHandle = msgHandler;
	}
	
	public void setInfo(String info) {
		this.INFO = info;
	}
	
	
	public void performTask() throws IOException {
		Variable var = varMap.get(Integer.valueOf(varID));
		
		if(TYPE.compareTo(TaskType.DEFAULT) == 0) {
			speak(INFO);
			
		} else if(TYPE.compareTo(TaskType.SET) == 0) {
			speak(INFO);
			setVariable(var);
		} else {
			speak(INFO);
			unSetVariable(var);
		}
	}
	
	public TaskType getType() {
		return this.TYPE;
	}
	private void setVariable(Variable var) throws IOException {
		// inform ASR about what it is about to hear
		String gram = "";
		LinkedList<String> valueSet = var.getValueSet();
		Iterator<String> iter = valueSet.iterator();
		int count = 0;
		while(iter.hasNext()) {
			if(count !=0) {
				gram += "|";
			}
			gram += iter.next();
			count++;
		}
		msgHandle.getPublisher().sendAction("R2D2_RECOGNIZE " + gram , 1, 1);
		
		// inform TTS to speak desired question
		String question = var.getQuestion();
		speak(question);
		
		// get user input
		DataCollector dc = new DataCollector();
		msgHandle.getSubscriber().addObserver(dc);
		
		// what to do while no input is received
		// I am arbitrarily asking it to wait 
		
		while(var.getValue().compareToIgnoreCase("$NDV$") == 0) {}
		
		System.out.println(var);
	}
	
	private boolean ifNumber(String input) {
	    try { 
	        Integer.parseInt(input); 
	    } catch(NumberFormatException e) { 
	    	return false; 
	    }
	    // only got here if we didn't return false
	    return true;
	}
	
	private void unSetVariable(Variable var) {
		var.setValue(var.NDV);
	}

	/**
	 * Speaks toSpeak through the microphone. Replace this with your own 
	 * code to forward it to other device
	 * @param toSpeak
	 * @throws IOException
	 */
	private void speak(String toSpeak) throws IOException {
		StringTokenizer st = new StringTokenizer(toSpeak);
		System.out.println("Speaking:" + toSpeak);
		Publisher pub = msgHandle.getPublisher();
		pub.sendAction("C3PO_SPEAK " + toSpeak, 2, 1);
		//Runtime.getRuntime().exec(new String[] { "say", toSpeak });
		try {
			Thread.currentThread().sleep(700 * st.countTokens());
		} catch (InterruptedException e) {
			System.out.println("Current thread unable to sleep");
		}
	}
	
	
	// Collects data from 
	class DataCollector implements Observer {

		
		public DataCollector() {
			
		}
		
		@Override
		public void update(Observable o, Object arg) {
			Variable var = varMap.get(Integer.valueOf(varID));
			Message input = (Message) arg;
			String data = input.getData();
			StringTokenizer st = new StringTokenizer(data);
			if(st.nextToken().compareToIgnoreCase("HANSOLO_SET") == 0){
				String value = "";
				while(st.hasMoreTokens()) {
					value += st.nextToken() + " ";
				}
				var.setValue(value);
			}
			
		}
		
	}
 }
