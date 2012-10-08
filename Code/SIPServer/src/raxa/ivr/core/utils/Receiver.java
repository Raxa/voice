/**
 * Copyright 2001 Sun Microsystems, Inc.
 * 
 * See the file "license.terms" for information on usage and
 * redistribution of this file, and for a DISCLAIMER OF ALL 
 * WARRANTIES.
 */

package raxa.ivr.core.utils;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.PrintWriter;

import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.sound.sampled.AudioFormat;


/**
 * Implements a Java Client for the Client/Server demo. For details about
 * the protocol between client and server, consult the file
 * <code>Protocol.txt</code>.
 */
public class Receiver extends Thread{

    private String serverAddress;
    private int serverPort;

    private static final int AUDIO_BUFFER_SIZE = 4143;				// magic number 4143
    
    
    private DataInputStream dataStream;     // for reading raw bytes
    private int sampleRate;
    private int sampleSize;            // in bits
    private byte[] socketBuffer = new byte[AUDIO_BUFFER_SIZE];
    
    private volatile BlockingQueue<byte[]> payload;
    private int count = 0;
    private int PAYLOAD_LIMIT = 10;
	


    /**
     * Constructs a default Client. It connects to the speech server, and
     * constructs an AudioPlayer.
     */
    public Receiver(String hostAddress, int hostPort,
    		int sampleRate, int sampleSize) {
		
    	this.serverAddress = hostAddress;
    	this.serverPort = hostPort;
    	this.sampleRate = sampleRate;
    	this.sampleSize = sampleSize;
    	this.payload = new LinkedBlockingQueue<byte[]>();
    	if (!connect()) {
		    System.out.println("Error connecting to " + serverAddress +
				       " at " + serverPort);
		    System.exit(1);
		}
    }
    
    public byte[] getData() {
    	byte[] data = payload.poll();
    	if(data == null) {
    		return new byte[AUDIO_BUFFER_SIZE];
    	}
    	return data;
    }
    

    /**
     * Connects this client to the server.
     *
     * @return  <code>true</code>  if successfully connected
     *          <code>false</code>  if failed to connect
     */
    private boolean connect() {
        try {
            Socket socket = new Socket(serverAddress, serverPort);
            dataStream = new DataInputStream(socket.getInputStream());
            return true;
        } catch (IOException ioe) {
	    ioe.printStackTrace();
	    return false;
        }
    }


    /**
     * Run the TTS protocol.
     */
    public void run() {
    	
    	while(true) {
    		receiveAudio(AUDIO_BUFFER_SIZE);
    	}
    }


    private void receiveAudio(int audioBufferSize) {
		
    	// Handle handshake protocols if any
    	boolean status = sendTTSRequest();
    	if(status) {
    		
    		byte[] buf = receive(audioBufferSize);
    		// wait while payload is still heavy
    		while(payload.size() > PAYLOAD_LIMIT) {};
    		
    		// now add data to payload
    		try {
				payload.put(buf);
			} catch (InterruptedException e) {				
				
			}
    	}		
	}

	/**
     * Sends a TTS request on the given text.
     *
     * @param text the text to do TTS on
     *
     * @return <code>true</code> if the TTS transaction was successful
     *         <code>false</code> if an error occurred
     */
    private boolean sendTTSRequest() {
	
		return true;
    }


    /**
     * Reads the given number of bytes from the socket, and plays them
     * with the AudioPlayer.
     *
     * @param numberSamples the number of bytes to read from the socket
     */
    private byte[] receive(int numberSamples) {
    	byte[] tmp = new byte[numberSamples];
    	try {
			int read = dataStream.read(tmp, 0, numberSamples);
		} catch (IOException e) {
			System.out.println("[Receiver] Can't read from socket ... ");
		}
    	
    	return tmp;		
    }

}