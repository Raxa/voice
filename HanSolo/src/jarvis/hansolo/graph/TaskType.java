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

public class TaskType implements Comparable<TaskType>{

	public static TaskType SET = new TaskType("SET");
	public static TaskType UNSET = new TaskType("UNSET");
	public static TaskType DEFAULT = new TaskType("DEFAULT");
	
	private String TYPE;
	
	private TaskType(String type){
		this.TYPE = type;
	}

	@Override
	public int compareTo(TaskType o) {
		if(this.TYPE == o.TYPE) {
			return 0;
		} else {
			return -1;
		}
		
	}
}
