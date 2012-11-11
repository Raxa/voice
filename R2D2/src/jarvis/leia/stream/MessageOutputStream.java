package jarvis.leia.stream;

import jarvis.leia.message.SimpleTextMessage;

/**
 * 
 * @author apurv
 *
 */
public abstract class MessageOutputStream {

	public abstract void writeTextMessage(SimpleTextMessage message) throws InterruptedException;
}
