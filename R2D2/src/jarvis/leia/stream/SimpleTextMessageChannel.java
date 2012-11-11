package jarvis.leia.stream;

import jarvis.leia.message.SimpleTextMessage;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * 
 * @author apurv
 *
 */
public class SimpleTextMessageChannel extends MessageChannel<SimpleTextMessage> {

	
	public SimpleTextMessageChannel(String channelName, int priority, int id) {
		this.CHANNEL_NAME = channelName;
		this.CHANNEL_PRIORITY = priority;
		this.CHANNEL_ID = id;
		this.data = new LinkedBlockingQueue<SimpleTextMessage>();
		this.inStream = new TextMessageInputStream(data);
		this.outStream = new TextMessageOutputStream(data);

	}
	
	
	@Override
	public MessageInputStream<SimpleTextMessage> getMessageInputStream() {
		
		return this.inStream;
	}

	@Override
	public MessageOutputStream getMessageOutputStream() {
		
		return this.outStream;
	}

}
