package jarvis.leia.stream;

import jarvis.leia.header.SimpleTextMessageHeader;
import jarvis.leia.message.ConsumerType;
import jarvis.leia.message.Message;
import jarvis.leia.message.SimpleTextMessage;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class Publisher {
	
	public String NAME;
	public int ID;
	public String HOST_ADDRESS;
	public int HOST_PORT;
	private Socket socket;
	private ObjectOutputStream oos;
	
	public Publisher(String hostAddress, int port) 
			throws UnknownHostException, IOException {
		this.HOST_ADDRESS = hostAddress;
		this.HOST_PORT = port;
		connect();
	}
	
	private boolean connect() throws UnknownHostException, IOException {
		this.socket = new Socket(HOST_ADDRESS, HOST_PORT);
		this.oos = new ObjectOutputStream(socket.getOutputStream());
		return true;
	}
	
	
	/**
	 * Executes the send message operation.
	 * @param msg Message that needs to be sent
	 * @param toID ID of the subscriber this msg is meant for
	 * @return true if operation was successful, false otherwise
	 */
	public boolean sendMessage(String msg, int toID, int priority) {
		try {
			ConsumerType destination = new ConsumerType(toID);
			SimpleTextMessageHeader header = new SimpleTextMessageHeader(ID, destination, priority);
			oos.writeObject(new SimpleTextMessage(header, msg));
		} catch (IOException e) {
			
			// If error was because of broken connection
			// reconnect
			if(socket.isClosed()) {
				try {
					connect();
				} catch (Exception e1) {
					System.out.println(e1.getMessage());
				}
			}							
			return false;
		}
		return true;
	}
	
	
	/**
	 * Same as sendMessage, only tries a number of times before giving up on sending the
	 * message to the desired host
	 * @param m Message to be sent
	 * @param retryCount number of tries for sending the message 
	 * @param priority priority of this message
	 * @return true if the message was sent, false otherwise
	 */
	public boolean sendMessage(String msg, int toID, int priority, int retryCount) {
		boolean sent = false;
		while(retryCount > 0 && !sent ) {
			sent = sendMessage(msg, toID, priority);
			retryCount--;
		}
		return sent;
	}
	
	
	/**
	 * Same as sendMessage, only tries a number of times before giving up on sending the
	 * message to the desired host. Sets priority as 0
	 * @param m Message to be sent
	 * @param retryCount number of tries for sending the message 
	 * @return true if the message was sent, false otherwise
	 */
	public boolean sendMessageNoPriority(String msg, int toID, int retryCount) {
		boolean sent = false;
		while(retryCount > 0 && !sent ) {
			sent = sendMessage(msg, toID, 0);
			retryCount--;
		}
		return sent;
	}
	
}
