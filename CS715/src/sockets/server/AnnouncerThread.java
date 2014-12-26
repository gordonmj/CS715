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

import project.Contestant;
import project.Host;
import project.Logger;
import project.Room;
import sockets.constants.Constants;

public class AnnouncerThread extends Logger implements Runnable {
	private Socket				mSocket			= null;
	private List<Contestant>	mContestants;							// list of contestants in order they were created
	private String				mName			= "AnnouncerThread";
	private double				mRightPercent	= 0.65;
	private PrintWriter			mOut;
	private BufferedReader		mIn;

	public static Object		intro			= new Object();

	public AnnouncerThread(Socket socket, PrintWriter out, BufferedReader in) {
		mSocket = socket;
		mOut = out;
		mIn = in;
	}

	@Override
	public void run() {
		try {
			String inputLine, outputLine;

			// Initiate conversation with client
			outputLine = Constants.CONNECTED;
			mOut.println(outputLine);

			boolean listening = true;
			List<Integer> scores = null;

			while ((inputLine = mIn.readLine()) != null) {
				String[] input = inputLine.split("\\|");

				switch (input[1]) {
				case Constants.GIVE_EXAMS:
					giveExams();
					mOut.println(Constants.EXAMS_GIVEN);
					break;
				case Constants.GRADE_EXAMS:
					scores = gradeExams();
					mOut.println(Constants.EXAMS_GRADED);
					break;
				case Constants.ANNOUNCE_SCORES:
					announceScores(scores);
					mOut.println(Constants.SCORES_ANNOUNCED);
					break;
				case Constants.START_GAME:
					startGame();
					listening = false;
					break;
				}
				if (!listening) break;
			}
		} catch (IOException e) {

		}

	}

	/**
	 * Give exams to {@link Contestant}s when the {@link Room} is full or when the final group is seated and not when another exam is in progress.
	 * When all test takers have taken the exam, no longer wait for others to take exam.
	 */
	private void giveExams() {
		log(mName + ": giving exams");
		while (true) {
			synchronized (Contestant.mRoom) {
				if (Contestant.mRoom.allTested()) break;
				if ((!Contestant.mRoom.areSeatsAvailable() || Contestant.mRoom.finalGroup()) && !Contestant.mRoom.examInProgress()) {
					log(mName + ": group is ready");
					Contestant.mRoom.notifyAll();
					Contestant.mRoom.setExamInProgress(true);
				}
			}
		}
	}

	/**
	 * Generates a list of randomly generated exam scores to be give to each test taker. Much like how most of QC does it. ha!
	 * 
	 * @return list of exam scores
	 */
	private List<Integer> gradeExams() {
		log(mName + ": grading exams");
		Random rand = new Random();
		List<Integer> scores = new ArrayList<Integer>();

		for (Contestant c : mContestants) {
			int score = rand.nextInt(101);
			log(mName + ": " + c.getName() + " has scored " + score);
			c.setExamScore(score);
			scores.add(score);
		}

		return scores;
	}

	/**
	 * Let each contestant know their score in the order they finished the exam.
	 * 
	 * @param scores
	 *            List of scores to be given to each contestant.
	 */
	private void announceScores(List<Integer> scores) {
		log(mName + ": announcing scores");
		Collections.sort(scores);
		Collections.reverse(scores);
		scores = scores.subList(0, 4);
		int spots = 4;

		for (ListIterator<Contestant> it = Contestant.mContestants.listIterator(); it.hasNext();) {
			Contestant c = it.next();
			int cScore = c.getExamScore();

			if (scores.contains(cScore) && spots > 0) {
				spots--;
				c.setPassedExam(true);
				log(mName + ": " + c.getName() + " has passed the exam");
			} else {
				c.setPassedExam(false);
				log(mName + ": " + c.getName() + " has failed the exam");
				it.remove();
			}
			synchronized (c.mConvey) {
				c.mConvey.notify();
			}
		}
	}

	/**
	 * Introduce each contestant that made it to the game. Then signal the Host to start the game.
	 */
	private void startGame() {
		log(mName + ": print an opening message (something useful, it is up to you)");
		new Thread(new Host(mRightPercent)).start();

		for (Contestant c : Contestant.mGame.getContestants()) {
			log(mName + ": Welcome " + c.getName() + " to the game.");
			synchronized (c.mConvey) {
				c.mConvey.notify();
			}

			synchronized (intro) {
				waitForSignal(intro, null);
			}
		}

		synchronized (Contestant.mGame) {
			Contestant.mGame.setGameStarted(true);
			Contestant.mGame.notify();
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
}
