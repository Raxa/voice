package jarvis.leia.message;

import java.io.Serializable;
import java.util.UUID;

/**
 * 
 * @author apurv
 *
 */
public class ConsumerType implements Serializable{
	
	
	public static final ConsumerType GLOBAL = new ConsumerType("GLOBAL" , 1);	// consumes all sorts of messages
	
	private String name;		// carries the name of the type of consumer
	
	private int id;		// id of this consumer type
	
	private int priority;	// higher is better. 
	
	public ConsumerType(int id) {		
		this(UUID.randomUUID().toString(), id);
	}
	
	public ConsumerType(String name, int id) {
		this(name, id, 0);
	}
	
	public ConsumerType(String name, int id, int priority) {
		this.name = name;
		this.id = id;
		this.priority = priority;
	}
	
	public String getConsumerName() {
		return name;
	}
	
	public int getPriority() {
		return priority;
	}
	
	public int getId() {
		return id;
	}
	
	@Override
	public String toString() {
		return name + "(ID: " + id + ", PRIORITY: " + priority + ")";
	}
}