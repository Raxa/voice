package jarvis.leia.stream;

import jarvis.leia.message.Message;

public abstract class MessageInputStream <T extends Message> {

	public abstract T readTextMessage();
}
