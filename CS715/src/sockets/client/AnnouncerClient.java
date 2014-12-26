package sockets.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import project.Logger;
import sockets.constants.Constants;

public class AnnouncerClient extends Logger implements Runnable {
	private String			mName	= "AnnouncerClient";

	private static String	mServername;
	private static int		mPort;

	public AnnouncerClient(String serverName, int port) {
		mServername = serverName;
		mPort = port;
	}

	@Override
	public void run() {
		try (Socket socket = new Socket(mServername, mPort);
				PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
				BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));) {
			String fromServer;
			boolean listening = true;

			while ((fromServer = in.readLine()) != null) {
				switch (fromServer) {
					case Constants.IDENTIFY:
						out.println(Constants.ANNOUNCER);
						break;
					case Constants.CONNECTED:
						log(mName + " connected to " + mServername);
						out.println(mName + "|" + Constants.GIVE_EXAMS);
						break;
					case Constants.EXAMS_GIVEN:
						log(mName + " finished giving exams");
						out.println(mName + "|" + Constants.GRADE_EXAMS);
						break;
					case Constants.EXAMS_GRADED:
						log(mName + " finished grading exams");
						out.println(mName + "|" + Constants.ANNOUNCE_SCORES);
						break;
					case Constants.SCORES_ANNOUNCED:
						log(mName + " scores announced");
						out.println(mName + "|" + Constants.START_GAME);
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
