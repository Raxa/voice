package raxa.ivr.core.utils;

import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;


/**
 * 
 * @author apurv
 *	This is a client for sending raw data real-time over TCP socket
 */
public class Sender extends Thread{
	
	private InetAddress address;	
	private int port;
	private boolean connected;
	
	private String uniqueID;
	private Socket socket;
	private OutputStream outStream;
	private DataOutputStream dos;
	
	private boolean verbose;
	private BlockingQueue<byte[]> payload;
	
	private boolean kill;		// in dire situations, you might want to kill sender
	
	/**
	 * Initialise with INET 4 address of server of server and the port it's listening to.
	 * @param inetAddress
	 * @param port
	 */
	public Sender(InetAddress inetAddress, int port, String id, boolean verbose) {
		this.address = inetAddress;
		this.port = port;
		this.uniqueID = id;
		this.connected = false;
		this.socket = null;
		this.verbose = verbose;
		this.payload = new LinkedBlockingQueue<byte[]>();
		this.kill = false;
	}
	
	public void send(byte[] data) throws InterruptedException {
		/*
		byte[] tmp = payload.poll();
		if(tmp != null) {
			ConcatenateArrays ca = new ConcatenateArrays();
			data = ca.concat(tmp, data);			
		}
		*/
		payload.put(data);
	}
	
	
	public void kill() {
		this.kill = true;
		this.connected = false;
		disconnect();
	}
	
	private void disconnect() {
		try {
			outStream.close();
			socket.close();
		} catch (IOException e) {
			System.out.println("ERROR:" + e.getMessage());
		}
	}
	
	/**
	 * A simple construction to connect to the server at the designated port
	 * @return
	 */
	private boolean connect() {
		while( !connected ) {
			try {
				socket = new Socket(address, port);
				socket.setKeepAlive(true);
				socket.setTcpNoDelay(true);
				socket.setPerformancePreferences(0, 2, 1);
				
				// Now that the connection is made, open channel to write data
				outStream = socket.getOutputStream();
				dos = new DataOutputStream(outStream);
				
				connected = true;
				// Okay we are now set to start writing data on this stream 
				// Prompt that maybe ;-)
				
				if(verbose) {
					System.out.println("Connected with the server... ");
				}
			
			} catch (IOException e) {
				//System.out.println("Unable to connect with server. Trying again ... ");				
			}
		}
		connected = true;
		return connected;
	}
	
	@Override
	public void run() {
		
		// First thing first. Connect with the server ;-)
		if(verbose)
			System.out.println("Attempting connection with the sever");
		
		connect();
		
		while(connected) {
			// send data while waiting for payload to be non-empty is necessary
			try {
				dos.write(payload.take());
				dos.flush();
			} catch (Exception e) {
				System.out.println("Exception:[Sender]" + e.getMessage());
				connected = false;
				connect();
			}
			
			// check if connection is still alive
			if(!socket.isConnected() && !kill) {
				connected = false;
				connect();
			}			
		}
		
	}
}
