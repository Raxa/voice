package jarvis.leia.stream;

import jarvis.leia.header.SimpleTextMessageHeader;
import jarvis.leia.message.ConsumerType;
import jarvis.leia.message.SimpleTextMessage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class Subscriber {
	
	public String NAME;
	public int ID;
	public String HOST_ADDRESS;
	public int HOST_PORT;
	private Socket socket;
	private ObjectInputStream ois;
	
	public Subscriber(String hostAddress, int port) 
			throws UnknownHostException, IOException {
		this.HOST_ADDRESS = hostAddress;
		this.HOST_PORT = port;
		connect();
	}
	
	private boolean connect() throws UnknownHostException, IOException {
		this.socket = new Socket(HOST_ADDRESS, HOST_PORT);
		this.ois = new ObjectInputStream(socket.getInputStream());
		return true;
	}
	
	
	/**
	 * Executes the get message operation.
	 * @param msg Message that needs to be sent
	 * @param toID ID of the subscriber this msg is meant for
	 * @return true if operation was successful, false otherwise
	 */
	public SimpleTextMessage sendMessage() {
		SimpleTextMessage message = null;
		try {
			message = (SimpleTextMessage) ois.readObject();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		} 
		return message;
	}
}
