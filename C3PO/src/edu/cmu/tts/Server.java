/*
 *
 * See the file "LICENSE" for information on usage and
 * redistribution of this file, and for a DISCLAIMER OF ALL
 * WARRANTIES.
 * 
 * Raxa.org
 *
 */

package edu.cmu.tts;

import com.sun.speech.freetts.Voice;
import com.sun.speech.freetts.VoiceManager;
import com.sun.speech.freetts.util.Utilities;

import edu.cmu.tts.utils.SocketAudioWriter;

import java.net.Socket;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;



/**
 * Implements a text-to-speech server for the Client/Server demo.
 * It creates a voice when it starts up (8k),
 * and then waits for socket connections.
 * After it receives a connection, it waits for TTS requests from the client,
 * does speech synthesis, and then sends the synthesized wave bytes back to
 * the client. For a complete specification of the protocol, please refer
 * to the document <code>Protocol.txt</code>.
 */
public class Server extends TTSServer {

    // 8k Voice
    private Voice voice;
    private String voice8kName = Utilities.getProperty
	("voice8kName", "kevin");

    public volatile BlockingQueue<String> prompts;
    private volatile boolean isConnected;
    
    private Socket dataSocket;
    private Socket controlSocket;
    
    // an AudioPlayer that writes bytes to the socket
    private SocketAudioWriter socketAudioPlayer;
    
    private boolean debug = false;;

    /**
     * Constructs a default Server, which loads an 8k Voice and a 16k Voice
     * by default.
     */
    public Server( Socket controlSocket, Socket dataSocket ) {
    	
		this.controlSocket = controlSocket;
		this.dataSocket = dataSocket;
		this.socketAudioPlayer = new SocketAudioWriter(controlSocket, dataSocket);
		
		this.prompts = new LinkedBlockingQueue<String>();
		this.isConnected = true;
		try {
	            VoiceManager voiceManager = VoiceManager.getInstance();
			    voice= voiceManager.getVoice(voice8kName);			    
	            voice.allocate();
	            
	            //voice.setAudioPlayer(socketAudioPlayer);
	    	    voice.setAudioPlayer(socketAudioPlayer);
	            
		} catch (Exception e) {
		    e.printStackTrace();
		    System.exit(1);
		}
    }
    
    public boolean isConnected() {
    	return isConnected;
    }
    
    /**
     * Adds next prompt to the queue to be processed
     */
    
    public void speak(String prompt) {    	
    	protocolHandler(prompt);
    }
    
    /**
     * Returns the 8k diphone voice.
     *
     * @return 8k diphone voice
     */
    public Voice getVoice() {
    	return voice;
    }


    /**
     * Spawns a ProtocolHandler depending on the current protocol.
     *
     * @param socket the socket that the spawned protocol handler will use
     */
    protected boolean protocolHandler( String prompt) {    	
	    if( !dataSocket.isClosed() && !controlSocket.isClosed()) {
	    	if(debug) {
	    		System.out.println("[Server] Speaking prompt.. ");
	    	}
	    	return voice.speak(prompt);
	    }
	    else {
	    	
	    	if( controlSocket.isClosed()) {
	    		System.out.println("control socket closed ");
	    	}
	    	if(debug)
	    		System.out.println( " Could not process prompt .. ");
	    	return false;
	    }
    }

}


