/*
 *
 * See the file "LICENSE" for information on usage and
 * redistribution of this file, and for a DISCLAIMER OF ALL
 * WARRANTIES.
 * 
 * Raxa.org
 *
 */

package jarvis.leia.stream;

import jarvis.leia.message.SimpleTextMessage;

import java.util.concurrent.BlockingQueue;

/**
 * 
 * @author apurv
 *
 */
public class TextMessageOutputStream extends MessageOutputStream{

	private volatile BlockingQueue<SimpleTextMessage> dataSync;
	private int QUEUE_MAX_SIZE;
	
	public TextMessageOutputStream(BlockingQueue<SimpleTextMessage> dataSync) {
		this(dataSync, Integer.MAX_VALUE);
	}
	
	public TextMessageOutputStream(BlockingQueue<SimpleTextMessage> dataSync, int maxSize) {
		this.dataSync = dataSync;
		this.QUEUE_MAX_SIZE = maxSize;
	}
	
	@Override
	public void writeTextMessage(SimpleTextMessage message) throws InterruptedException {
		if(dataSync.size() >= QUEUE_MAX_SIZE)
			dataSync.poll();
		
		dataSync.put(message);
	}
}
