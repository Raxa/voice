package jarvis.leia.header;

import jarvis.leia.message.ConsumerType;

public class SimpleTextMessageHeader extends Header {
	
	public SimpleTextMessageHeader( int publisherID, ConsumerType destination) {
		this(publisherID, destination, 1);
	}
	
	public SimpleTextMessageHeader(int publisherID, ConsumerType destination, int priority) {
		this.PRIORITY = priority;
		this.PUBLISHER_ID = publisherID;
		this.CONSUMER_TYPE = destination;
		this.TIME_STAMP = System.currentTimeMillis();
	}

}
