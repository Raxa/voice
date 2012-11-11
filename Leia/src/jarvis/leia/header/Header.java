/*
 *
 * See the file "LICENSE" for information on usage and
 * redistribution of this file, and for a DISCLAIMER OF ALL
 * WARRANTIES.
 * 
 * Raxa.org
 *
 */

package jarvis.leia.header;

import java.io.Serializable;

import jarvis.leia.message.ConsumerType;
import jarvis.leia.message.MessageType;

public abstract class Header implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public ConsumerType CONSUMER_TYPE;	// represents the consumer type the message will 
										// cater to. 
	public int PRIORITY;		// represents the priority of this message
	
	public int PUBLISHER_ID;
	
	public MessageType MSG_TYPE;
	
	public long TIME_STAMP;
	
	public ConsumerType getConsumerType() {
		return this.CONSUMER_TYPE;
	}
	
	public int getPriority() {
		return this.PRIORITY;
	}
	
	@Override
	public String toString() {
		return " PUBLISHER \t\t\t:\t\t" + PUBLISHER_ID + "\n " +
				"CONSUMER TYPE \t\t:\t\t" +	CONSUMER_TYPE.toString() + "\n " +
						"PRIORITY \t\t\t:\t\t" + PRIORITY + "\n " +
							"MESSAGE TYPE \t\t:\t\t" + MSG_TYPE +"\n " +
								"TIME STAMP \t\t:\t\t" + TIME_STAMP; 
						
	}
	
	public long getTimeStamp() {
		return TIME_STAMP;
	}
	
	public MessageType getMessageType() {
		return this.MSG_TYPE;
	}
	
}
