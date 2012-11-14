/*
 *
 * Copyright 1999-2004 Carnegie Mellon University.
 * Portions Copyright 2004 Sun Microsystems, Inc.
 * Portions Copyright 2004 Mitsubishi Electric Research Laboratories.
 * All Rights Reserved.  Use is subject to license terms.
 *
 * See the file "license.terms" for information on usage and
 * redistribution of this file, and for a DISCLAIMER OF ALL
 * WARRANTIES.
 *
 */

package edu.cmu.tts.demo;

import jarvis.leia.header.Header;
import jarvis.leia.message.Message;
import jarvis.leia.message.MessageType;
import jarvis.leia.message.SimpleTextMessage;
import jarvis.leia.stream.Publisher;
import jarvis.leia.stream.Subscriber;

import java.io.IOException;
import java.net.Socket;
import java.util.Observable;
import java.util.Observer;
import java.util.StringTokenizer;

import com.sun.speech.freetts.Voice;
import com.sun.speech.freetts.VoiceManager;
import com.sun.speech.freetts.util.Utilities;

import edu.cmu.tts.Server;

public class HandleSocketClient implements Observer {
	
	private Socket controlSocket;
	private Socket dataSocket;
	private Publisher pub;
	private Subscriber sub;
	private Server server;
	
	private Voice voice;
    private String voice8kName = Utilities.getProperty
	("voice8kName", "kevin");
    
	public HandleSocketClient(Socket controlSocket, Socket dataSocket, Publisher pub, Subscriber sub) {
		this.controlSocket = controlSocket;
		this.dataSocket = dataSocket;
		this.pub = pub;
		this.sub = sub;
		
		// Add this Socket client as a observer to the set of observers of subscriber
		this.sub.addObserver(this);
		try {
            VoiceManager voiceManager = VoiceManager.getInstance();
		    this.voice= voiceManager.getVoice(voice8kName);			    
            this.voice.allocate();
            
		} catch (Exception e) {
		    e.printStackTrace();
		    System.exit(1);
		}
	}
	
	/**
	 * 
	 * @param data
	 * @return
	 */
	private boolean performAction(String action) {	
	
		System.out.println("Speaking:" + action);
		server.speak(action);
		return true;
	}

	@Override
	public void update(Observable o, Object msg) {
		if(!dataSocket.isClosed() && !controlSocket.isClosed()) {
			server = new Server(controlSocket, dataSocket,voice);
    		Message message = (Message) msg;
    		if(message!=null) {
	    		Header header = message.getHeader();
	    		String data = message.getData();
	    		StringTokenizer st = new StringTokenizer(data);
	    		String tmp = st.nextToken();
	    		String COMMAND = "C3PO_SPEAK";
	    		if(tmp.compareTo(COMMAND) == 0) {
	    			if(header.getMessageType().compareTo(MessageType.ACTION) == 0) {
	    			String action = "";
	    			while(st.hasMoreTokens()) {
	    				action += st.nextToken() + " ";
	    			}
	    			performAction( action );
	    			pub.sendAction("HANSOLO NEXT", 1, 1);
	    			}
	    		}
    		}
		}
		
	}
}
