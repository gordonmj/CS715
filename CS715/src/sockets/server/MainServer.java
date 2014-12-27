package sockets.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;

import sockets.constants.Constants;

public class MainServer {

	public static void main(String[] args) {
		boolean listening = true;

		try (ServerSocket serverSocket = new ServerSocket(Constants.PORT)) {
			System.out.println("Main Server Running on " + InetAddress.getLocalHost().getHostName() + " on port #" + Constants.PORT);
			while (listening) {
				new Thread(new ServerThread(serverSocket.accept())).start();
			}
		} catch (IOException e) {
			System.err.println("Could not listen on port " + Constants.PORT);
			System.exit(-1);
		}
	}
}