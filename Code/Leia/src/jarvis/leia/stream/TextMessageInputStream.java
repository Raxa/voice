package jarvis.leia.stream;

import jarvis.leia.message.SimpleTextMessage;

import java.util.concurrent.BlockingQueue;

public class TextMessageInputStream extends MessageInputStream{

	private volatile BlockingQueue<SimpleTextMessage> dataSource;
	public TextMessageInputStream (BlockingQueue<SimpleTextMessage> dataSource) {
		this.dataSource = dataSource;
	}
	
	@Override
	public SimpleTextMessage readTextMessage() {
		return dataSource.poll();
	}

}
