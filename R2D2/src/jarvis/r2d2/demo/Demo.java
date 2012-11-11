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

package jarvis.r2d2.demo;

import jarvis.leia.stream.MessageHandler;
import jarvis.leia.stream.Publisher;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import edu.cmu.sphinx.frontend.util.Microphone;
import edu.cmu.sphinx.linguist.language.ngram.SimpleNGramModel;
import edu.cmu.sphinx.recognizer.Recognizer;
import edu.cmu.sphinx.result.Result;
import edu.cmu.sphinx.util.props.ConfigurationManager;

public class Demo {
	
	public static void main(String args[]) throws Exception {
		
		test();
		
		/*
		MessageHandler messageHandler = new MessageHandler("R2D2", 3, "localhost", 1089, 1090);
		Publisher publisher = messageHandler.getPublisher();
		
		ServerSocket controlServer = new ServerSocket(9990);
		ServerSocket dataServer = new ServerSocket(9991);
		while(true) {
			Socket controlSocket = controlServer.accept();
			Socket dataSocket = dataServer.accept();
			ASRServer asrServer = new ASRServer(dataSocket.getInputStream());
			asrServer.start();
			System.out.println("[R2D2] Start ..");
			while(!dataSocket.isClosed() && !controlSocket.isClosed()) {
				String result = asrServer.recognize();
				if(result != null) {
					System.out.println("R2D2_RESULT: " + result);
					publisher.sendInfo("HANSOLO_SET " + result, 1, 1);
				}
			}		
			
		}
		*/
	}
	
	
	
	public static void test() throws Exception{
		
		MessageHandler messageHandler = 
				new MessageHandler("R2D2", 3, "luckyluke.pc.cs.cmu.edu", 1089, 1090);
		Publisher publisher = messageHandler.getPublisher();
		ConfigurationManager cm;
        cm = new ConfigurationManager("./config/r2d2.config.xml");
        
        Recognizer recognizer = (Recognizer) cm.lookup("recognizer");
        
        Microphone microphone = (Microphone) cm.lookup("microphone");        
        
        SimpleNGramModel slm = (SimpleNGramModel) cm.lookup("trigramModel");
        slm.setLanguageModel("/Users/DeathStar/Documents/workspace/R2D2/lib/ques-comm.arpa");
        
        recognizer.allocate();
        System.out.println(recognizer);
       
        if (!microphone.startRecording()) {
            System.out.println("Cannot start microphone.");
            recognizer.deallocate();
            System.exit(1);
        }
        
        // loop the recognition until the programm exits.
        while (true) {
            System.out.println("Start speaking. Press Ctrl-C to quit.\n");

            Result result = recognizer.recognize();

            if (result != null) {
                String resultText = result.getBestFinalResultNoFiller();
                System.out.println("You said: " + resultText + '\n');
                publisher.sendAction("HANSOLO_SET " + resultText, 1, 1);
            } else {
                System.out.println("I can't hear what you said.\n");
            }
        }
        
    }

}
