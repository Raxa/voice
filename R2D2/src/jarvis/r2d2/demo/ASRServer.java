/*
 * Copyright 1999-2002 Carnegie Mellon University.  
 * Portions Copyright 2002 Sun Microsystems, Inc.  
 * Portions Copyright 2002 Mitsubishi Electric Research Laboratories.
 * All Rights Reserved.  Use is subject to license terms.
 * 
 * See the file "license.terms" for information on usage and
 * redistribution of this file, and for a DISCLAIMER OF ALL 
 * WARRANTIES.
 *
 */

package jarvis.r2d2.demo;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import edu.cmu.sphinx.frontend.util.StreamDataSource;
import edu.cmu.sphinx.recognizer.Recognizer;
import edu.cmu.sphinx.util.props.ConfigurationManager;

public class ASRServer extends Thread{

	private InputStream inStream;
	private Recognizer recognizer;
	StreamDataSource sdc;
	private int RESULTS_SIZE = 20;				// keeps a check on the size of results list
	private volatile BlockingQueue<String> results;
	
	public ASRServer(InputStream inStream) {
		this.inStream = inStream;
		ConfigurationManager cm = new ConfigurationManager("./config/r2d2.config.xml");
        this.recognizer = (Recognizer) cm.lookup("recognizer");
        this.sdc = (StreamDataSource) cm.lookup("audioSource");
        
        init();        
	}
	
	private void init() {
		this.results = new LinkedBlockingQueue<String>();
		recognizer.allocate();        
        sdc.setInputStream(inStream, "networkAudioStream");
	}
	
	public void setInputStream(InputStream inStream){
		this.inStream = inStream;
		try {
			recognizer.deallocate(); 
		} catch (IllegalStateException ise) {
			
		}
		init();
	}
	
	public void run() {
		while(true) {
			
			
			boolean stdout = false;
			try {
				System.out.println("[R2D2.ASRServer] Listening ... ");
				String result = recognizer.recognize().getBestFinalResultNoFiller();
				if(results.size() > RESULTS_SIZE) {
					stdout = true;
					System.out.println("[R2D2.ASRServer] Too many results in queue. Waiting for queue to get empty");
				}
				while(results.size() > RESULTS_SIZE) {} ;
				
				results.put(result);
				if(stdout) {
					System.out.println("Added result ..");
					stdout = false;
				}
			} catch (Exception e) {
				System.out.println("[R2D2.ASRServer]" + e.getMessage());
				break;				
			}
		}
		
	}
	
	public String recognize() {
		return results.poll();
	}
}