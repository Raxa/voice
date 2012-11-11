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

import jarvis.leia.message.Message;

import java.util.concurrent.BlockingQueue;

/**
 * 
 * @author apurv
 *
 * @param <T>
 */
public abstract class MessageChannel<T extends Message> {

	public String CHANNEL_NAME;
	public int CHANNEL_ID;
	public int CHANNEL_PRIORITY;
	public volatile BlockingQueue<T> data;
	public MessageInputStream inStream;
	public MessageOutputStream outStream;

	public String getName() {
		return this.CHANNEL_NAME;
	}
	
	public int getChannelId() {
		return CHANNEL_ID;
	}
	
	public int getPriority() {
		return CHANNEL_PRIORITY;
	}
	
	public abstract MessageInputStream getMessageInputStream();
	
	public abstract MessageOutputStream getMessageOutputStream();
	
}
