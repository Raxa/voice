package raxa.voice.utils;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

public class WavReader {
	
	private String fileName;
	private AudioInputStream audioInputStream;
	
	public WavReader(String fileName) 
			throws UnsupportedAudioFileException, IOException {
		this.fileName = fileName;
		this.audioInputStream = AudioSystem.getAudioInputStream(
				new File(fileName));
	}
	
	/**
	 * We want getData to return data on each call, but if we don't have any data
	 * it should not stall the process instead send a null String.
	 * @return
	 */
	
	public AudioInputStream getAudioInputStream() {
		return audioInputStream;
	}
}
