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

import jarvis.leia.message.ConsumerType;
import jarvis.leia.message.MessageType;

/**
 * 
 * @author apurv
 *
 */
public class SimpleTextMessageHeader extends Header {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public SimpleTextMessageHeader( int publisherID, ConsumerType destination) {
		this(publisherID, destination, 1, MessageType.ACTION);
	}
	
	public SimpleTextMessageHeader(int publisherID, ConsumerType destination, int priority, MessageType msgType) {
		this.PRIORITY = priority;
		this.PUBLISHER_ID = publisherID;
		this.CONSUMER_TYPE = destination;
		this.TIME_STAMP = System.currentTimeMillis();
		this.MSG_TYPE = msgType;
	}

}
