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
import jarvis.leia.stream.MessageHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.StringTokenizer;

public class TaskNode {

	public int ID;
	public LinkedList<TaskNode> successors;
	public HashMap<Integer, Variable> varMap;
	public String INFO;
	private Task task;
	private String condition;
	private int NUM_OF_UNMATCHED_CONDITIONS;
	
	public TaskNode (int id, HashMap<Integer, Variable> varMap, 
			Task task, String condition, String info) {
		this.ID = id;
		this.successors = new LinkedList<TaskNode>();
		this.varMap = varMap;	
		this.INFO = info;
		this.task = task;
		this.condition = condition;
		this.task.setVarMap(varMap);
		this.NUM_OF_UNMATCHED_CONDITIONS = Integer.MAX_VALUE;
	}
	
	public Task getTask() {
		return this.task;
	}
	// TODO: Modify this function to add more conditional checks
	public boolean checkCondition() {
		NUM_OF_UNMATCHED_CONDITIONS = 0;
		StringTokenizer st = new StringTokenizer(condition);
		while(st.hasMoreTokens()) {
			String cond = st.nextToken();
			
			// take variable to check value of
			cond = cond.substring(1);
			String varId = cond.substring(0, cond.indexOf("$"));
			
			Integer vID = Integer.valueOf(varId);
			Variable var = varMap.get(vID);
			
			// take conditional parameter
			cond = cond.substring(cond.indexOf("$")+1);
			String comparator = cond.substring(0, 1);
			
			// make comparisons based on comparator "=" "!" 
			if(comparator.compareTo("=") == 0) {
				// what value to compare to
				cond = cond.substring(cond.indexOf("=") + 1);
				if(var.getValue().compareToIgnoreCase(cond) != 0) {
					NUM_OF_UNMATCHED_CONDITIONS ++;
				}
			} else if(comparator.compareTo("!") == 0) {
				// what value to compare to
				cond = cond.substring(cond.indexOf("!") + 1);
				if(var.getValue().compareToIgnoreCase(cond) == 0) {
					//System.out.println("here");
					NUM_OF_UNMATCHED_CONDITIONS ++;
				}
			}
			
		}
		if(NUM_OF_UNMATCHED_CONDITIONS > 0) {
			return false;
		}
		return true;
	}
	
	public int getNumUnMatchedConditions() {
		return NUM_OF_UNMATCHED_CONDITIONS;
	}
	
	// Adds successor to the current task node
	public void addSuccessor( TaskNode successor) {
		this.successors.add(successor);
	}
	
	public TaskNode nextTask() {
		Iterator<TaskNode> iter = successors.iterator();
		TaskNode nextNode = null;
		while(iter.hasNext()){
			TaskNode node = iter.next();
			node.checkCondition();
			if(node.NUM_OF_UNMATCHED_CONDITIONS == 0) {
				return node;
			} 			
		}
		if(nextNode == null) {
			System.out.println("Dialog Ended ...");
		}
		return nextNode;
	}
	
	public void performTask(MessageHandler msgHandle) throws IOException {
		this.task.setInfo(INFO);
		this.task.setMessageHandler(msgHandle);
		this.task.performTask();
	}
	
	
}
