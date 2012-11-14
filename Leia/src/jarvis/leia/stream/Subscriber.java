package jarvis.leia.stream;




import jarvis.leia.message.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Observable;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 
 * @author apurv
 *
 */
public class Subscriber extends Observable{
	
	public String NAME;
	public int ID;
	public String HOST_ADDRESS;
	public int HOST_PORT;
	private Socket socket;
	private ObjectInputStream ois;
	private boolean debug = false;
	private MessageCollector mc;
	
	public Subscriber(String hostAddress, int port, String name, int id) 
			throws UnknownHostException, IOException {
		this.HOST_ADDRESS = hostAddress;
		this.HOST_PORT = port;
		this.ID = id;
		this.NAME = name;
		connect();
		mc = new MessageCollector();
		this.mc.start();
		
	}
	
	/**
	 * Connects the subscriber to the messaging server
	 * @return
	 * @throws UnknownHostException
	 * @throws IOException
	 */
	private boolean connect() throws UnknownHostException, IOException {
		this.socket = new Socket(HOST_ADDRESS, HOST_PORT);
		this.ois = new ObjectInputStream(socket.getInputStream());
		return true;
	}
	
	
	private void printMessage(Object msg) {
		System.out.println(" -------- [" + NAME + ".Subscriber] ----------");
		System.out.println(msg);
	}
	
	class MessageCollector extends Thread {
		
		@Override 
		public void run() {
			while(true) {
				Object obj = null;
				try {
					obj =ois.readObject();
					if(obj != null) {
						// Surprisingly it sometimes is null?? 
						
						if (!( obj instanceof Message)) {
							System.out.println("************** UNEXPECTED *****************");
							System.out.println(obj);
						} else {
							Message message = (Message) obj;
							setChanged();
							notifyObservers(message);
							if(debug) {
								printMessage(message);
							}
						}
					}
				} catch (Exception e) {
					// Report any sort of Exception to stdOut
					System.out.println("Why was the input from Stream:" + obj);
					e.printStackTrace();
				} 
			}
		}
	}
}
