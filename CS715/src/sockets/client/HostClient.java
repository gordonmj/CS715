package sockets.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import project1.Logger;
import sockets.constants.Constants;

public class HostClient extends Logger implements Runnable {
	private String			mName;

	private static String	mServername;
	private static int		mPort;

	public HostClient(String serverName, int port) {
		mName = "HostClient";
		mServername = serverName;
		mPort = port;
	}

	@Override
	public void run() {

		try (Socket socket = new Socket(mServername, mPort);
				PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
				BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));) {
			String fromServer = "";
			boolean listening = true;

			while ((fromServer = in.readLine()) != null) {
				switch (fromServer) {
				case Constants.IDENTIFY:
					out.println(mName + "|" + Constants.HOST);
					break;
				case Constants.CONNECTED:
					log(mName + " connected to " + mServername);
					out.println(mName + "|" + Constants.WAIT_FOR_GAME_START);
					break;
				case Constants.GAME_STARTED:
					log(mName + " game started");
					out.println(mName + "|" + Constants.GAME_PLAY_HOST);
					break;
				case Constants.GOODBYE:
					listening = false;
					break;
				}
				if (!listening) break;
			}
		} catch (UnknownHostException e) {
			System.err.println("Don't know about host " + mServername);
			System.exit(1);
		} catch (IOException e) {
			System.err.println("Couldn't get I/O for the connection to " + mServername);
			System.exit(1);
		}
	}

}
