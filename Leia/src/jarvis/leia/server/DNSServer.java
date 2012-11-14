package jarvis.leia.server;


import java.io.IOException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

import jarvis.leia.message.Message;
import jarvis.leia.message.MessageType;
import jarvis.leia.stream.MessageHandler;
import jarvis.leia.stream.Publisher;
import jarvis.leia.stream.Subscriber;

public class DNSServer implements Observer{
	
	public final int id = 1;
	public final String NAME = "DNS";
	private MessageHandler messageHandler;
	private HashMap<String, Integer> dns;
	private Publisher pub;
	private Subscriber sub;
	
	public DNSServer (String hostAddress, int publisherPort, int subscriberPort) 
			throws UnknownHostException, IOException {
		
		this.messageHandler = new MessageHandler(NAME, id, hostAddress, publisherPort, subscriberPort);
		this.dns = new HashMap<String, Integer>();
		
		pub = messageHandler.getPublisher();
		
		sub = messageHandler.getSubscriber();
		sub.addObserver(this);
		
		// initialise dns with it's own entry 
		dns.put(NAME, id);
	}
	
	

	@Override
	public void update(Observable o, Object obj) {
		Message message = (Message) obj;
		MessageType mType = message.getHeader().getMessageType();
		
		
		// If the new component is registering, add it to DNS
		if(mType.compareTo(MessageType.REGISTER) == 0) {
			String name = message.getHeader().getConsumerType().getName();
			int id = message.getHeader().getConsumerType().getId();
			if(dns.containsKey(name)) {
				System.out.println("Replacing previously existing entry for " + name);					
			}				
			dns.put(name, id);
			pub.sendInfo("REGISTERED: " + name + "( ID: " +  id + " )", 1, 1);			
		}
		
		System.out.println("------------------ DNS SERVER LOG START ----------------------");
		System.out.println(message);
		System.out.println("------------------ DNS SERVER LOG STOP -----------------------");
		
	}
	
	
}
