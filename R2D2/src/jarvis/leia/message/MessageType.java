package jarvis.leia.message;

import java.io.Serializable;

/**
 * 
 * @author apurv
 *
 */
public class MessageType implements Serializable, Comparable<MessageType>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final MessageType REGISTER = new MessageType("REGISTER", 1);
	public static final MessageType ACTION = new MessageType("ACTION" , 2);
	public static final MessageType INFO = new MessageType("INFO", 3);
	
	private int code;
	private String type;
	private MessageType(String type, int code) {
		this.type = type;
		this.code = code;
	}
	
	public int getCode() {
		return this.code;
	}
	
	@Override
	public String toString() {
		return "(Type : " + type + " , Code: " + code + " )";
	}

	@Override
	public int compareTo(MessageType o) {
		
		return (o.getCode() - this.code);
	}
}
