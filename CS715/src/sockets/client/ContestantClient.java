package sockets.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import project.Logger;
import sockets.constants.Constants;

public class ContestantClient extends Logger implements Runnable {
	private String			mName;

	private static String	mServername;
	private static int		mPort;

	public ContestantClient(String name, String serverName, int port) {
		mName = "ContestantClient" + name;
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
					out.println(mName + "|" + Constants.CONTESTANT);
					break;
				case Constants.CONNECTED:
					log(mName + " connected to " + mServername);
					out.println(mName + "|" + Constants.FORM_GROUP);
					break;
				case Constants.GROUP_FORMED:
					log(mName + " group was formed");
					out.println(mName + "|" + Constants.TAKE_EXAM);
					break;
				case Constants.EXAM_TAKEN:
					log(mName + " exam finished");
					out.println(mName + "|" + Constants.WAIT_FOR_RESULTS);
					break;
				case Constants.GOODBYE:
					listening = false;
					break;
				case Constants.OK_FOR_GAME:
					log(mName + " ok for the game");
					out.println(mName + "|" + Constants.WAIT_FOR_INTRO);
					break;
				case Constants.INTRO_GIVEN:
					log(mName + " intro given");
					out.println(mName + "|" + Constants.GAME_PLAY);
					break;
				}
				if (!listening) break;
			}
			log(mName + " shutting down");
		} catch (UnknownHostException e) {
			System.err.println("Don't know about host " + mServername);
			System.exit(1);
		} catch (IOException e) {
			System.err.println("Couldn't get I/O for the connection to " + mServername);
			System.exit(1);
		}
	}

}
