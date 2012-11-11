/*
 *
 * See the file "LICENSE" for information on usage and
 * redistribution of this file, and for a DISCLAIMER OF ALL
 * WARRANTIES.
 * 
 * Raxa.org
 *
 */

package jarvis.leia.demo;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Observable;
import java.util.Observer;

import jarvis.leia.message.MessagePool;
import jarvis.leia.server.DNSServer;
import jarvis.leia.server.Server;
import jarvis.leia.stream.MessageHandler;
import jarvis.leia.stream.Publisher;
import jarvis.leia.stream.Subscriber;

public class Demo {
	public static void main(String Args[]) throws UnknownHostException, IOException {
		Server server = new Server(1089, 1090);
		DNSServer dns = new DNSServer("localhost", 1089, 1090);
		
		while(true) {}
	}

	
}
