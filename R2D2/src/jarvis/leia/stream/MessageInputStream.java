package jarvis.leia.stream;

import jarvis.leia.message.Message;

/**
 * 
 * @author apurv
 *
 * @param <T>
 */
public abstract class MessageInputStream <T extends Message> {

	public abstract T readTextMessage();
}
