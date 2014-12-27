package sockets.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import project.Game;
import project.Logger;
import project.Room;
import sockets.constants.Constants;

public class ContestantThread extends Logger implements Runnable {
	private String			mName			= "ContestantThread";
	private Socket			mSocket			= null;
	private PrintWriter		mOut;
	private BufferedReader	mIn;

	public static Room		mRoom			= new Room();
	public static Game		mGame			= new Game();

	private static int		mNumContestants	= -1;
	private static long		mExamTime		= 5000L;
	private static int		mQuestionValues	= 200;

	public ContestantThread(Socket socket, PrintWriter out, BufferedReader in) {
		mSocket = socket;
		mOut = out;
		mIn = in;
		
		synchronized (mRoom) {
			if (mNumContestants == -1) { // first contestant needs to set parameters
				mNumContestants = 1;
				mRoom.setRoomCapacity(1);
				mRoom.setTotalExams2Give(1);
				mGame.setNumQuestions(3);
				mGame.setNumRounds(3);
				mQuestionValues = 200;
			}
		}
	}

	public void run() {
		try {
			String inputLine, outputLine;

			// Initiate conversation with client
			outputLine = Constants.CONNECTED;
			mOut.println(outputLine);

			while ((inputLine = mIn.readLine()) != null) {
				String[] input = inputLine.split("\\|");
				switch (input[1]) {
				case Constants.FORM_GROUP:
					formGroup(input[0]);
					mOut.println(Constants.GROUP_FORMED);
					break;
				case Constants.TAKE_EXAM:
					takeExam();
					mOut.println(Constants.EXAM_TAKEN);
					break;
				}
			}
		} catch (IOException e) {

		}

	}

	private void formGroup(String contestant) {
		log(mName + ": forming group");
		while (true) {
			synchronized (mRoom) {
				if (mRoom.areSeatsAvailable() && !mRoom.examInProgress()) {
					mRoom.fillSeat();
					waitForSignal(mRoom, "waiting to take exam");
					break;
				}
			}
		}
	}

	private synchronized void takeExam() {
		log(mName + ": taking exam");
		try {
			this.wait(mExamTime);
		} catch (InterruptedException e) {

		}
		synchronized (mRoom) {
			mRoom.examTaken();
		}
	}

	/**
	 * Must be called within a synchronized block
	 * 
	 * @param obj
	 *            - Object to block on
	 */
	private void waitForSignal(Object obj, String comment) {
		while (true) {
			try {
				obj.wait();
				break;
			} catch (InterruptedException e) {
				continue;
			}
		}
	}

	static String[]	readyQuotes	= { "Ready to play!", "Let's ROCK!", "I already won this game.", "I am awesome!", "Really? I made it?",
			"Where is my prize money?", "Thank you, thank you", "Let's drop the hammer", "Jean....can you hear me?", "I like to parrrr-taaayyy" };
}