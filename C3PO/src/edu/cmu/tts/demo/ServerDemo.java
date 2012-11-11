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

import jarvis.leia.stream.Publisher;
import jarvis.leia.stream.Subscriber;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import edu.cmu.tts.Server;


/**
 * Instantiates a Text to Speech server 
 * @author apurv
 *
 */
public class ServerDemo {

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
    	Publisher pub = new Publisher("localhost", publisherPort, "TTS", 1);
    	Subscriber sub = new Subscriber("localhost", subscriberPort, "TTS", 1);
    	while(true){
    		Socket controlSocket = controlServerSocket.accept();
    		Socket dataSocket = dataServerSocket.accept();
    		System.out.println("Accepted a connection ... ");
    		HandleSocketClient hsc = new HandleSocketClient(controlSocket, dataSocket, pub, sub);
    		hsc.start();    		
    	}
    }    
    
}
