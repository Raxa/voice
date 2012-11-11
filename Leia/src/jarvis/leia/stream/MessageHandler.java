/*
 *
 * See the file "LICENSE" for information on usage and
 * redistribution of this file, and for a DISCLAIMER OF ALL
 * WARRANTIES.
 * 
 * Raxa.org
 *
 */

package jarvis.leia.stream;

import java.io.IOException;
import java.net.UnknownHostException;

public class MessageHandler {
	private String NAME;
	private int ID;
	private Publisher publisher;
	private Subscriber subscriber;
	
	
	public MessageHandler(String name, int id, String hostAddress, 
			int publisherPort, int subscriberPort) 
					throws UnknownHostException, IOException {
		this.NAME = name;
		this.ID = id;
		this.publisher = new Publisher(hostAddress, publisherPort, name, id);
		this.subscriber = new Subscriber(hostAddress, subscriberPort, NAME, ID);
		
	}
	
	public Publisher getPublisher() {
		return publisher;
	}
	
	public Subscriber getSubscriber() {
		return subscriber;
	}

}
