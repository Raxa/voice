package jarvis.leia.server;

import jarvis.leia.message.Message;
import jarvis.leia.message.MessagePool;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Creates a server to listen to socket 
 * @author apurv
 *
 */
public class Server {
	
	private ServerSocket publisherSocket;
	private ServerSocket subscriberSocket;
	private MessagePool msgPool;
	private boolean debug = false;
	
	public Server (int publisherPort, int subscriberPort) throws IOException {
		this.publisherSocket = new ServerSocket(publisherPort);
		this.subscriberSocket = new ServerSocket(subscriberPort);
		this.msgPool = new MessagePool();
		PublisherAcceptor pa = new PublisherAcceptor();
		pa.start();
		SubscriberAcceptor sa = new SubscriberAcceptor();
		sa.start();
	}
	
	class PublisherAcceptor extends Thread{
		
		public PublisherAcceptor() {
			
		}
		
		@Override
		public void run() {
			while(true) {
				try {
					if(debug)
						System.out.println("[PublisherAcceptor] Waiting for more publishers connections... ");
					Socket socket = publisherSocket.accept();
					HandlePublisher hp = new HandlePublisher(socket);
					hp.start();
				} catch (IOException e) {
					System.out.println("[PublisherAcceptor] " + e.getMessage());
				}
			}
		}
	}
	
	
	/**
	 * Defines the protocol of to deal with incoming publishers.
	 * The messages coming from a publisher are forwarded to the message pool
	 * which then forwards that to the subscribers. 
	 *
	 */
	class HandlePublisher extends Thread {
		private Socket socket;
		private ObjectInputStream ois;
		public HandlePublisher (Socket socket) throws IOException {
			this.socket = socket;
			this.ois = new ObjectInputStream(socket.getInputStream());
			if(debug)
				System.out.println("[HandlePublisher] Publisher accepted ...");
		}
		
		@Override
		public void run() {
			while(!socket.isClosed()) {
				try {
					Message message = (Message) ois.readObject();
					msgPool.postMessage(message);
				} catch (Exception e) {
					System.out.println("[HandlePublisher]" + e.getMessage());
					break;
				} 
			}
			System.out.println("[HandlePublisher] Publisher socket closed ... ");
		}
	}
	
	
	class SubscriberAcceptor extends Thread{
		@Override
		public void run() {
			while(true) {
				try {
					if(debug)
						System.out.println("[SubscriberAcceptor] Waiting for more subscribers connections... ");
					Socket socket = subscriberSocket.accept();
					HandleSubscriber hs = new HandleSubscriber(socket);
					msgPool.addObserver(hs);
				} catch (IOException e) {
					System.out.println("[SubscriberAcceptor]" + e.getMessage());
				}
			}
		}
	}
	
	
	
	class HandleSubscriber implements Observer {
		
		private Socket socket;
		private ObjectOutputStream oos;
		public HandleSubscriber(Socket socket) throws IOException {
			this.socket = socket;
			this.oos = new ObjectOutputStream(this.socket.getOutputStream());
			if(debug)
				System.out.println("[HandleSubscriber] New Subscriber added .. ");
		}
		
		@Override
		public void update(Observable o, Object message) {
			if(debug)
				System.out.println("[HandleSubscriber] New Message");
			try {
				oos.writeObject(message);
			} catch (IOException e) {
				System.out.println("[HandleSubscriber]" + e.getMessage());				
				
				// If this socket is closed, the subscriber probably quit.
				// remove it from the list of observers then				
				msgPool.deleteObserver(this);
				
			}
			
		}
	}
	
}
