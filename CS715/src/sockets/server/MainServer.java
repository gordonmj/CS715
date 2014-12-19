package sockets.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import sockets.constants.Constants;

public class MainServer {

	public static void main(String[] args) {
		boolean listening = true;

		try (ServerSocket serverSocket = new ServerSocket(Constants.PORT)) {
			System.out.println("Main Server Running on " + InetAddress.getLocalHost().getHostName() + " on port #" + Constants.PORT);
			while (listening) {
				try (Socket socket = serverSocket.accept()) {
					identify(socket);
				} catch (IOException e) {

				}
			}
		} catch (IOException e) {
			System.err.println("Could not listen on port " + Constants.PORT);
			System.exit(-1);
		}

	}

	private static void identify(Socket socket) {
		try {
			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			String inputLine, outputLine;

			// Initiate conversation with client
			outputLine = Constants.IDENTIFY;
			out.println(outputLine);

			while ((inputLine = in.readLine()) != null) {
				switch (inputLine) {
					case Constants.ANNOUNCER:
						new AnnouncerThread(socket).run();
						break;
					case Constants.HOST:
						new ContestantThread(socket).run();
						break;
					case Constants.CONTESTANT:
						new ContestantThread(socket).run();
						break;
				}
			}
		} catch (IOException e) {

		}
	}
}