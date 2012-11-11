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
