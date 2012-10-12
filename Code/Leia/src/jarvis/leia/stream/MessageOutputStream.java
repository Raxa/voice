package jarvis.leia.stream;

import jarvis.leia.message.SimpleTextMessage;

public abstract class MessageOutputStream {

	public abstract void writeTextMessage(SimpleTextMessage message) throws InterruptedException;
}
