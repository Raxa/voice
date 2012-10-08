/*
    This file is part of Peers, a java SIP softphone.

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
    
    Copyright 2008, 2009, 2010, 2011 Yohann Martineau 
*/

package net.sourceforge.peers.media;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PipedOutputStream;
import java.util.concurrent.CountDownLatch;

import javax.sound.sampled.AudioInputStream;

import raxa.ivr.core.utils.ConcatenateArrays;
import raxa.ivr.core.utils.Receiver;
import raxa.voice.utils.WavReader;
import raxa.voice.utils.WavWriter;


import net.sourceforge.peers.Logger;
import net.sourceforge.peers.nat.Client;


public class Capture implements Runnable {
    
    public static final int SAMPLE_SIZE = 16;
    public static final int BUFFER_SIZE = SAMPLE_SIZE * 20;
    public int DEFAULT_AUDIO_BUFFER_SIZE = 4143;			// magic number 4143?
    
    private PipedOutputStream rawData;
    private boolean isStopped;
    private SoundManager soundManager;
    private Logger logger;
    private CountDownLatch latch;
    private Receiver server;
    private WavReader reader;
    
    public Capture(PipedOutputStream rawData, SoundManager soundManager,
            Logger logger, CountDownLatch latch, Receiver server, WavReader reader) {
        this.rawData = rawData;
        this.soundManager = soundManager;
        this.logger = logger;
        this.latch = latch;
        isStopped = false;
        this.server = server;
        this.reader = reader;
    }
    
    

    public void run() {        
        while (!isStopped) {
            // buffer = soundManager.readData();
            try {
            	byte[] data = server.getData();
            	
            	if (data != null) {  
            		while(data.length > 0) {
            			int length = DEFAULT_AUDIO_BUFFER_SIZE;
            			if(data.length < length) {
            				length = data.length;
            			}
            			byte[] buffer = new byte[length];
            			System.arraycopy(data, 0, buffer, 0, length);
            			byte[] tmp = new byte[data.length - length];
            			System.arraycopy(data, length, tmp, 0, data.length - length);
      	              	data = tmp;
            			//rawData.write(new byte[160]);
                        rawData.write(buffer);
                        rawData.flush();
            		}
            		
            	} else {
            		System.out.println("null");
            		data = new byte[DEFAULT_AUDIO_BUFFER_SIZE];
            		rawData.write(data);
            		rawData.flush();
            	}
            	
                
            } catch (IOException e) {
                logger.error("input/output error", e);
                return;
            }
        }
        latch.countDown();
        if (latch.getCount() != 0) {
            try {
                latch.await();
            } catch (InterruptedException e) {
                logger.error("interrupt exception", e);
            }
        }
    }

    public synchronized void setStopped(boolean isStopped) {
        this.isStopped = isStopped;
    }

}
