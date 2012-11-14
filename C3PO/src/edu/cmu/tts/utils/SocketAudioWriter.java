

package edu.cmu.tts.utils;


import com.sun.speech.freetts.audio.AudioPlayer;

import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import java.net.Socket;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;

import raxa.voice.utils.WavWriter;



/**
 * Implements the AudioPlayer for the freetts Client/Server demo.
 * This SocketAudioPlayer basically sends synthesized wave bytes to the
 * client.
 */
public class SocketAudioWriter implements AudioPlayer {

    private Socket dataSocket;
    private Socket controlSocket;
    private DataOutputStream dataOutStream;
    private PrintWriter writer;
    private int bytesToWrite;
    private int bytesWritten;
    private byte[] buffer;

    private boolean debug = false;
    
    private volatile boolean socketIsConnected;
    

    /**
     * Constructs a SocketAudioPlayer that will send wave bytes to the
     * given Socket.
     *
     * @param socket the Socket to which synthesized wave bytes will be sent
     */
    public SocketAudioWriter(Socket controlSocket, Socket dataSocket) {
		this.controlSocket = controlSocket;
		this.dataSocket = dataSocket;
		this.bytesToWrite = 0;
		this.bytesWritten = 0;
		
		try {
		    this.dataOutStream = new DataOutputStream(dataSocket.getOutputStream());
		    this.writer = new PrintWriter(controlSocket.getOutputStream(), true);
		    socketIsConnected = true;
		} catch (IOException ioe) {
		    socketIsConnected = false;
		}		
    }
    
    
    public boolean connectionStatus(){
    	return socketIsConnected;
    }
    
    /**
     * This is receives the first call when data is to be sent to socket
     * @param size total size of packet to be sent. Remember, the total 
     * 			size is usually broken into multiple calls to write()
     * 			before end() is called
     */    
	@Override
	public void begin(int size) {
		this.bytesToWrite = size;
		bytesWritten = 0;
		buffer = new byte[bytesToWrite];
		if(debug)
			System.out.println("Begin: write " + bytesToWrite + " bytes to stream");
		// first tell the client how much is it about hear. 
		// # Only if it were true in boring conversations.

		
		
		if(debug)
			System.out.print(String.valueOf(size) + "\n");
	}


	@Override
	public void cancel() {
		
	}


	@Override
	public void close() {
		
	}


	@Override
	public boolean drain() {
		
		return true;
	}
	
	
	/**
	 * Called when the entire packet is sent. Simply flush the entire stream when 
	 * all the packets are received
	 */
	@Override
	public boolean end() {		
		
		try {
			dataOutStream.write(buffer);
			dataOutStream.flush();
			//WavWriter ww = new WavWriter("tts-prompt.wav",	new ByteArrayInputStream(buffer));
			//ww.write();
		} catch (IOException e) {
			socketIsConnected = false;
		}
		writer.write( bytesToWrite + "\n");
		writer.flush();
		if(debug) 
			System.out.println("End: Written data to stream");
		
		return true;
	}


	@Override
	public AudioFormat getAudioFormat() {
		return null;
	}


	@Override
	public long getTime() {
		return 0;
	}


	@Override
	public float getVolume() {
		
		return 0;
	}


	@Override
	public void pause() {
		
	}


	@Override
	public void reset() {
		
		
	}


	@Override
	public void resetTime() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void setAudioFormat(AudioFormat arg0) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void setVolume(float arg0) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void showMetrics() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void startFirstSampleTimer() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public boolean write(byte[] audioData) {
		
		return write(audioData, 0, audioData.length);
	}


	@Override
	public boolean write(byte[] audioData, int offset, int length) {
		
		if(debug)
			System.out.println("write");
		
		byte[] tmp = new byte[length];
		System.arraycopy(audioData, offset, tmp, 0, length);
		tmp = changeBig2LittleEndian(tmp);
		
		System.arraycopy(tmp, 0, buffer, bytesWritten, length);
		bytesWritten += length;
		
		
		/*
		try {
			dataOutStream.write(tmp);
			dataOutStream.flush();
			writer.write( tmp.length + "\n");
			writer.flush();
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		*/
		return true;
	}
	
	
	public byte[] changeBig2LittleEndian(byte[] buf) {
        	byte[] array = new byte[buf.length];
			if(buf.length % 2 == 1) {
				System.out.println("[SocketAudioWriter] Why is audio length odd?");
			}
			for(int i = 0; i < buf.length -1 ; i += 2){
				array[i] = buf[i+1];
				array[i+1] = buf[i];
			}
    		return array;
    	}


}