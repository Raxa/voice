package jarvis.leia.server;

import jarvis.leia.message.SimpleTextMessage;
import jarvis.leia.stream.Publisher;
import jarvis.leia.stream.SimpleTextMessageChannel;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

	public static void main(String Args[]) throws IOException {
		ServerImpl server = new ServerImpl(1089);
		server.start();
		System.out.println();
		Publisher publisher = new Publisher("localhost", 1089);
		for(int i = 0; i< 10; i++) {
			publisher.sendMessage(i + "", 1, 1);
		}
	}
	
	public static class ServerImpl extends Thread{
		
		public ServerSocket serverSocket;
		public SimpleTextMessageChannel channel;
		public ObjectInputStream ois;
		public ObjectOutputStream oos;
		
		public ServerImpl (int port) throws IOException {
			serverSocket = new ServerSocket(port);						
		}
		
		public void connect() throws IOException {
			Socket socket = serverSocket.accept();
			ois = new ObjectInputStream(socket.getInputStream());
			oos = new ObjectOutputStream(socket.getOutputStream());
			channel = new SimpleTextMessageChannel("simple channel", 1, 1);
		}
		
		@Override
		public void run(){
			try {
				connect();
			} catch (IOException e) {
				System.out.println(e.getMessage());
			}
			while(true) {
				try {
					SimpleTextMessage message = (SimpleTextMessage) ois.readObject();
					System.out.println("================= MSG START ======================");
					System.out.println(message);
					System.out.println("--------------------------------------------------");
				} catch (Exception e) {
					System.out.println(e.getMessage());
				}
			}
		}
	}
}
