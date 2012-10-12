package jarvis.leia.demo;

import jarvis.leia.header.SimpleTextMessageHeader;
import jarvis.leia.message.ConsumerType;
import jarvis.leia.message.SimpleTextMessage;
import jarvis.leia.stream.SimpleTextMessageChannel;
import jarvis.leia.stream.TextMessageInputStream;
import jarvis.leia.stream.TextMessageOutputStream;

public class Demo {
	
	public static void main(String Args[]) throws InterruptedException {
		SimpleTextMessageChannel channel = new SimpleTextMessageChannel("GENERAL", 1, 1);
		TextMessageInputStream inStream = (TextMessageInputStream) channel.getMessageInputStream();
		TextMessageOutputStream outStream = (TextMessageOutputStream) channel.getMessageOutputStream();
		
		outStream.writeTextMessage(new SimpleTextMessage(new SimpleTextMessageHeader("Bruce Wayne", ConsumerType.GLOBAL), "Hi! I am Batman!"));
		System.out.println(inStream.readTextMessage());
	}

}
