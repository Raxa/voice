/*
 *
 * See the file "LICENSE" for information on usage and
 * redistribution of this file, and for a DISCLAIMER OF ALL
 * WARRANTIES.
 * 
 * Raxa.org
 *
 */

package jarvis.hansolo.demo;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;
import java.util.StringTokenizer;

import jarvis.hansolo.graph.Task;
import jarvis.hansolo.graph.TaskNode;
import jarvis.hansolo.graph.TaskType;
import jarvis.hansolo.utils.tools.Variable;
import jarvis.hansolo.utils.tools.VariableType;
import jarvis.leia.header.Header;
import jarvis.leia.message.Message;
import jarvis.leia.message.MessageType;
import jarvis.leia.stream.MessageHandler;
import jarvis.leia.stream.Publisher;
import jarvis.leia.stream.Subscriber;

public class Demo implements Observer{
	
	private MessageHandler msgHandle;
	private volatile boolean CONTINUE_DIALOG; // is a variable to reset the dialog
	private volatile boolean NEXT;		// ensures that the dialog does not proceed to the next step until asked to do so
	
	public Demo() throws UnknownHostException, IOException {
		this.msgHandle = new 
				MessageHandler("HANSOLO", 11, "luckyluke.pc.cs.cmu.edu", 1089, 1090);
		this.msgHandle.getSubscriber().addObserver(this);
		this.CONTINUE_DIALOG = false;
		this.NEXT = false;
	}
	
	private MessageHandler getMessageHandler() {
		return this.msgHandle;
	}
	
	private boolean getContinueDialog() {
		return this.CONTINUE_DIALOG;
	}
	
	private boolean getNext() {
		return this.NEXT;
	}
	
	/**
	 * Resets value of next
	 * @param next
	 */
	private void setNext(boolean next) {
		this.NEXT = next;
	}
	
	public static void main(String Args[]) throws Exception {
		
		Demo server = new Demo();
		MessageHandler msgHandle = server.getMessageHandler();
		while(true) {
			//System.out.println("CONTINUE Dialog: " + server.getContinueDialog());
			if(server.getContinueDialog())
				routine(msgHandle, server);
		}
		 
	}
	
	public static void routine(MessageHandler msgHandle, Demo server) throws UnknownHostException, IOException{
		
		
		/** 
		 * Define variables
		 */
		Variable var1 = new Variable(1, "COMPANY_NAME", VariableType.STR);		
		var1.setValue("Raxa");
		Variable var2 = new Variable(2, "USER_NAME", VariableType.STR);
		var2.setQuestion("Can I know your name please");
		var2.setConfirmation("Hello $val");
		var2.setPossibleValues("++NAME++");
		Variable var3 = new Variable(3, "USER_AGE", VariableType.INT);
		var3.setQuestion("What is your age ");
		var3.setConfirmation("I have your age as $val" );
		var3.setPossibleValues("++INT++");
		Variable var4 = new Variable(4, "USER_GENDER", VariableType.STR);
		var4.setQuestion("What is your gender ");
		var4.setConfirmation("So you are a $val");
		var4.setPossibleValues("Male");
		var4.setPossibleValues("Female");
		
		/**
		 * put variables in a hashmap
		 */
		HashMap<Integer, Variable> varMap = new HashMap<Integer, Variable>();
		varMap.put(1, var1);
		varMap.put(2, var2);
		varMap.put(3, var3);
		varMap.put(4, var4);
		
		/**
		 * Define task nodes
		 */
		Task t1 = new Task(TaskType.DEFAULT, -1);
		Task t2 = new Task(TaskType.DEFAULT, -1);
		Task t3 = new Task(TaskType.SET, 2);
		Task t4 = new Task(TaskType.SET, 3);
		Task t5 = new Task(TaskType.SET, 4);
		Task t6 = new Task(TaskType.DEFAULT, -1);
		TaskNode root = new TaskNode(0, varMap, t1, "", "Hello. This is JARVIS");
		TaskNode n1 = new TaskNode(1, varMap, t2, "$1$=Raxa", "Welcome to Raxa");
		TaskNode n2 = new TaskNode(2, varMap, t3, "$1$=Raxa", "Let's fill out a medical information form");
		TaskNode n3 = new TaskNode(3, varMap, t4, "$1$=Raxa $2$!$NDV$", "");
		TaskNode n4 = new TaskNode(4, varMap, t5, "$2$!$NDV$ $3$=$NDV$", "You seem to be a girl, anyway");
		TaskNode n5 = new TaskNode(5, varMap, t5, "$2$!$NDV$ $3$!$NDV$", "Thanks for giving me your age");
		// TaskNode t6 = new TaskNode(6, varMap, t6, "$1$=Raxa $2$$$NDV$ $4$!$NDV$", "Thank you. I have your information. We will get back to you soon");
		
		msgHandle.getPublisher().sendInfo("Defined dialog task nodes. Generating dialog task graph", 1, 0);
		
		/**
		 * Define Task topology
		 */
		root.addSuccessor(n1);
		n1.addSuccessor(n2);
		n2.addSuccessor(n3);
		n3.addSuccessor(n4);
		n3.addSuccessor(n5);
		
		msgHandle.getPublisher().sendInfo("Dialog graph prepared", 1, 1);
		msgHandle.getPublisher().sendInfo("HANSOLO Ready", 1, 1);
		
		/**
		 * Execute task
		 */
		TaskNode node = root;
		while(node != null && server.getContinueDialog()){
			node.performTask(msgHandle);
			if(node.getTask().getType().compareTo(TaskType.DEFAULT) == 0)  {
				
				server.setNext(false);
			}
			node = node.nextTask();
		}
		
		msgHandle.getPublisher().sendInfo("Dialog complete...", 1, 1);
		msgHandle.getPublisher().sendAction("ALL SHUTDOWN", 1, 1);
	}

	@Override
	public void update(Observable o, Object arg) {
		Message msg = (Message) arg;
		Header header = msg.getHeader();
		if(header.getMessageType().compareTo(MessageType.ACTION) == 0) {
			String data = msg.getData();
			StringTokenizer st = new StringTokenizer(data);
			if(st.nextToken().compareTo("HANSOLO") == 0) {
				// print message
				System.out.println(msg);
				String command = st.nextToken();
				System.out.println("[HanSolo] Command:" + command);
				if(command.compareToIgnoreCase("STOP") == 0) {
					CONTINUE_DIALOG = false;
				} else if(command.compareToIgnoreCase("START") == 0) {
					CONTINUE_DIALOG = true;
				} else if(command.compareTo("NEXT") == 0) {
					NEXT = true;
				}
			}
		}
	}
}
