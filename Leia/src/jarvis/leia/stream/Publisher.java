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

import jarvis.leia.header.SimpleTextMessageHeader;
import jarvis.leia.message.ConsumerType;
import jarvis.leia.message.Message;
import jarvis.leia.message.MessageType;
import jarvis.leia.message.SimpleTextMessage;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * 
 * @author apurv
 *
 */
public class Publisher {
	
	public String NAME;
	public int ID;
	public String HOST_ADDRESS;
	public int HOST_PORT;
	private Socket socket;
	private ObjectOutputStream oos;
	
	public Publisher(String hostAddress, int port, String name, int id) 
			throws UnknownHostException, IOException {
		this.HOST_ADDRESS = hostAddress;
		this.HOST_PORT = port;
		this.ID = id;
		this.NAME = name;
		connect();
	}
	
	private boolean connect() throws UnknownHostException, IOException {
		this.socket = new Socket(HOST_ADDRESS, HOST_PORT);
		this.oos = new ObjectOutputStream(socket.getOutputStream());
		// First register yourself as at DNS
		register();
		return true;
	}
	
	
	public void register () {
		sendMessage(ID + "", 1, 1, MessageType.REGISTER);
	}
	
	/**
	 * Executes the send message operation.
	 * @param msg Message that needs to be sent
	 * @param toID ID of the subscriber this msg is meant for
	 * @return true if operation was successful, false otherwise
	 */
	private boolean sendMessage(String msg, int toID, int priority, MessageType msgType) {
		try {
			ConsumerType destination = new ConsumerType(NAME, toID);
			SimpleTextMessageHeader header = new SimpleTextMessageHeader
					(ID, destination, priority, msgType);
			oos.writeObject(new SimpleTextMessage(header, msg));
			oos.flush();
		} catch (IOException e) {
			
			// If error was because of broken connection
			// reconnect
			if(socket.isClosed()) {
				try {
					connect();
				} catch (Exception e1) {
					System.out.println("[Publisher] " + e1.getMessage());
				}
			}							
			return false;
		}
		return true;
	}
	
	/**
	 * 
	 * @param action
	 * @param toID
	 * @param priority
	 */
	public void sendAction(String action, int toID, int priority) {
		sendMessage(action, toID, priority, MessageType.ACTION);
	}
	
	public void sendInfo(String info, int toID, int priority) {
		sendMessage(info, toID, priority, MessageType.INFO);
	}
	
	
	
}
