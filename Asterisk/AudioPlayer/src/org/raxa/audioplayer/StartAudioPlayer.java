package org.raxa.audioplayer;

import org.asteriskjava.fastagi.DefaultAgiServer;

public class StartAudioPlayer {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		DefaultAgiServer agi=new DefaultAgiServer();
		agi.run();
	}

}
