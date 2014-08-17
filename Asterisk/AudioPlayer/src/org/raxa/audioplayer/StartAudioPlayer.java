package org.raxa.audioplayer;

import org.asteriskjava.fastagi.DefaultAgiServer;

/**
 * Class which initiates CallHandler
 * @author rahulr92
 *
 */
public class StartAudioPlayer {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		DefaultAgiServer agi=new DefaultAgiServer();
		agi.run();
	}

}
