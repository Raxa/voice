/*
 *
 * See the file "LICENSE" for information on usage and
 * redistribution of this file, and for a DISCLAIMER OF ALL
 * WARRANTIES.
 * 
 * Raxa.org
 *
 */

package edu.cmu.tts.demo;

import jarvis.leia.header.Header;
import jarvis.leia.message.MessageType;
import jarvis.leia.message.SimpleTextMessage;
import jarvis.leia.stream.Publisher;
import jarvis.leia.stream.Subscriber;

import java.io.IOException;
import java.net.Socket;

import edu.cmu.tts.Server;

public class HandleSocketClient extends Thread {
	
	private Socket controlSocket;
	private Socket dataSocket;
	private Publisher pub;
	private Subscriber sub;
	private Server server;
	public HandleSocketClient(Socket controlSocket, Socket dataSocket, Publisher pub, Subscriber sub) {
		this.controlSocket = controlSocket;
		this.dataSocket = dataSocket;
		this.pub = pub;
		this.sub = sub;
	}
	
	@Override
	public void run() {
		while(!dataSocket.isClosed() && !controlSocket.isClosed()) {
			server = new Server(controlSocket, dataSocket);
    		System.out.println("Assigned a voice ... ");
    		SimpleTextMessage message = sub.getMessage();
    		Header header = message.getHeader();
    		if(header.getMessageType().compareTo(MessageType.ACTION) == 0) {
    			String data = message.getData();
    			System.out.println("Speaking: " + data);
    			performAction( data);
    		}
    		
    		// Close if one of these sockets is still active
    		
			try {
				if(!controlSocket.isClosed()) {
					controlSocket.close();
				}
				if ( !dataSocket.isClosed()) {
        			dataSocket.close();
        		}
			} catch (IOException e) {
				
			}
		}
		System.out.println("Connection lost ...");
		
	}
	
	/**
	 * 
	 * @param data
	 * @return
	 */
	private boolean performAction(String data) {
		String tmp = data.substring(0, data.indexOf(" "));
		String COMMAND = "TTS_SPEAK";
		if(tmp.compareTo(COMMAND) == 0) {
			String action = data.substring(data.indexOf(" "));
			System.out.println("Action:" + action);
			server.speak(action);
			return true;
		}
		return false;
	}
}
