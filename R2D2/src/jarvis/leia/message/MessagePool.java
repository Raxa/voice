package jarvis.leia.message;

import java.util.Observable;

public class MessagePool extends Observable{
	private Message message;
	public void postMessage(Message message) {
		this.message = message;
		setChanged();
		notifyObservers(message);
	}
}
