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

import jarvis.leia.stream.MessageHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


public class TextToSpeechServer {

	/**
     * Starts this TTS Server.
	 * @throws IOException 
     */
    public static void main(String[] args) throws IOException {
    	int dataPort = 9993;
    	int controlPort = 9992;
    	int publisherPort = 1089;
    	int subscriberPort = 1090;
    	
    	ServerSocket controlServerSocket = new ServerSocket(controlPort);
    	ServerSocket dataServerSocket = new ServerSocket(dataPort);
    	MessageHandler messageHandler = new MessageHandler("TTS", 2, "localhost", publisherPort, subscriberPort);
    	while(true) {
    		Socket controlSocket = controlServerSocket.accept();
    		Socket dataSocket = dataServerSocket.accept();
    		System.out.println("[TextToSpeechServer] Invoking new TTS server for this connection ... ");
    		HandleSocketClient hsc = new HandleSocketClient(
    				controlSocket, dataSocket, messageHandler.getPublisher(), messageHandler.getSubscriber());
    		System.out.println("[TextToSpeechServer] HandleSocketClient started for this client");
    	}
    }    
    
}
