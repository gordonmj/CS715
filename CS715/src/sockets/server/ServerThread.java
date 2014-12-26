package sockets.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;

import project.Game;
import project.Logger;
import project.Room;
import sockets.constants.Constants;

public class ServerThread extends Logger implements Runnable {
	private Socket				mSocket;
	private String				mName;
	private List<ServerThread>	mContestants;					// list of contestants in order they were created
	private double				mRightPercent	= 0.65;

	public static Room			mRoom			= new Room();
	public static Game			mGame			= new Game();
	public static Object		intro			= new Object();

	private static int			mNumContestants	= -1;
	private static long			mExamTime		= 5000L;
	private static int			mQuestionValues	= 200;

	public ServerThread(Socket socket) {
		mSocket = socket;

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

	@Override
	public void run() {
		try {
			PrintWriter out = new PrintWriter(mSocket.getOutputStream(), true);
			BufferedReader in = new BufferedReader(new InputStreamReader(mSocket.getInputStream()));
			String inputLine, outputLine;

			// Initiate conversation with client
			outputLine = Constants.CONNECTED;
			out.println(outputLine);

			List<Integer> scores = null;

			while ((inputLine = in.readLine()) != null) {
				String[] input = inputLine.split("\\|");
				switch (input[1]) {
				case Constants.FORM_GROUP:
					formGroup(input[0]);
					out.println(Constants.GROUP_FORMED);
					break;
				case Constants.TAKE_EXAM:
					takeExam();
					out.println(Constants.EXAM_TAKEN);
					break;
				case Constants.GIVE_EXAMS:
					// giveExams();
					out.println(Constants.EXAMS_GIVEN);
					break;
				case Constants.GRADE_EXAMS:
					// scores = gradeExams();
					out.println(Constants.EXAMS_GRADED);
					break;
				case Constants.ANNOUNCE_SCORES:
					// announceScores(scores);
					out.println(Constants.SCORES_ANNOUNCED);
					break;
				case Constants.START_GAME:
					// startGame();
					// listening = false;
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
