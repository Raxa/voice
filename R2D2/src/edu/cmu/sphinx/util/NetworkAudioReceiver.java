package edu.cmu.sphinx.util;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;

public class NetworkAudioReceiver extends Thread{

	private Socket controlSocket;
	private Socket dataSocket;
	
	private BufferedReader reader;
	private DataInputStream dis;
	private LinkedBlockingQueue<byte[]> payload;
	private int PAYLOAD_LIMIT = 10000;			// This is just to limit the payload to acquire huge memory
	
	public NetworkAudioReceiver(Socket controlSocket, 
			Socket dataSocket) throws IOException {
		this.controlSocket = controlSocket;
		this.dataSocket = dataSocket;
		reader = new BufferedReader(
				new InputStreamReader(controlSocket.getInputStream()));
		dis = new DataInputStream(dataSocket.getInputStream());
		this.payload = new LinkedBlockingQueue<byte[]>();
	}
	
	public void run() {
		
		while(!controlSocket.isClosed() && !dataSocket.isClosed()) {
			int size;
			try {
				size = executeControlProtocol();
				collectData(size);
			} catch (Exception e) {
				try {
					controlSocket.close();
				} catch (IOException e1) {
					
				}
				try {
					dataSocket.close();
				} catch (IOException e1) {
					
				}
				
			}
			
		}
	}
	
	public byte[] getData() throws InterruptedException {
		System.out.println("[NetworkAudioReceiver] getData called ...");
		return payload.take();
	}
	
	/**
	 * Control protocol informs the receiver how much to read from the data stream.
	 * @return size
	 * @throws IOException 
	 */
	private int executeControlProtocol() throws IOException {
		String line = reader.readLine();
		int size = Integer.valueOf(line);
		return size;
	}
	
	private void collectData(int size) throws Exception {
		byte[] buf = new byte[size];
		dis.read(buf);
		
		while(payload.size() > PAYLOAD_LIMIT) {};
		payload.put(buf);
		//System.out.println("[NetworkAudioReceiver] added audio to payload .. ");
	}
}
