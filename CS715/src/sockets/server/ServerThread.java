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

import project.Answer;
import project.Game;
import project.Logger;
import project.Room;
import sockets.constants.Constants;

public class ServerThread extends Logger implements Runnable {
	private Socket						mSocket;
	private String						mName;

	// Contestant variables
	private int							mExamScore;
	private boolean						mPassedExam		= false;
	public Object						mConvey			= new Object();
	private boolean						mDismiss		= false;
	private Integer						mGameScore		= 0;
	private int							mFinalWager		= -1;
	private boolean						mGiveIntro		= false;

	// Host variables
	public static Answer				answer			= new Answer();
	public static Object				question		= new Object();

	public static Room					mRoom			= new Room();
	public static Game					mGame			= new Game();
	public static Object				intro			= new Object();

	private static List<ServerThread>	mContestants	= Collections.synchronizedList(new ArrayList<ServerThread>());
	private static List<ServerThread>	mExamOrder		= Collections.synchronizedList(new ArrayList<ServerThread>());
	private static int					mNumContestants	= 13;
	private static long					mExamTime		= 5000L;
	private static int					mQuestionValues	= 200;

	public ServerThread(Socket socket) {
		mSocket = socket;

		synchronized (mRoom) {
			if (mRoom.getRoomCapacity() == -1) { // first contestant needs to set parameters
				mRoom.setRoomCapacity(4);
				mRoom.setTotalExams2Give(mNumContestants);
				mGame.setNumQuestions(3);
				mGame.setNumRounds(3);
				mQuestionValues = 200;
			}
		}
	}

	@Override
	public void run() {
		try (PrintWriter out = new PrintWriter(mSocket.getOutputStream(), true);
				BufferedReader in = new BufferedReader(new InputStreamReader(mSocket.getInputStream()));) {
			String inputLine, outputLine;

			// Initiate conversation with client
			outputLine = Constants.IDENTIFY;
			out.println(outputLine);

			List<Integer> scores = null;

			while ((inputLine = in.readLine()) != null) {
				String[] input = inputLine.split("\\|");
				switch (input[1]) {
					case Constants.CONTESTANT:
						mContestants.add(this);
						mName = input[0];
						out.println(Constants.CONNECTED);
						break;
					case Constants.ANNOUNCER:
						mName = input[0];
						out.println(Constants.CONNECTED);
						break;
					case Constants.HOST:
						mName = input[0];
						out.println(Constants.CONNECTED);
						break;

					// CONTESTANT COMMUNICATION
					case Constants.FORM_GROUP:
						formGroup(input[0]);
						out.println(Constants.GROUP_FORMED);
						break;
					case Constants.TAKE_EXAM:
						takeExam();
						out.println(Constants.EXAM_TAKEN);
						break;
					case Constants.WAIT_FOR_RESULTS:
						waitForResults();
						out.println(mDismiss ? Constants.GOODBYE : Constants.OK_FOR_GAME);
						break;
					case Constants.WAIT_FOR_INTRO:
						waitForIntroduction();
						out.println(Constants.INTRO_GIVEN);
						break;
					case Constants.GAME_PLAY:
						roundPlay();
						finalRound();
						out.println(Constants.GOODBYE);
						mDismiss = true;
						break;

					// ANNOUNCER COMMUNICATION
					case Constants.GIVE_EXAMS:
						giveExams();
						out.println(Constants.EXAMS_GIVEN);
						break;
					case Constants.GRADE_EXAMS:
						scores = gradeExams();
						out.println(Constants.EXAMS_GRADED);
						break;
					case Constants.ANNOUNCE_SCORES:
						announceScores(scores);
						out.println(Constants.SCORES_ANNOUNCED);
						break;
					case Constants.START_GAME:
						startGame();
						out.println(Constants.GOODBYE);
						mDismiss = true;
						break;

					// HOST COMMUNICATION
					case Constants.WAIT_FOR_GAME_START:
						waitForGame2Start();
						out.println(Constants.GAME_STARTED);
						break;

					case Constants.GAME_PLAY_HOST:
						roundPlayHost();
						finalRoundHost();
						out.println(Constants.GOODBYE);
						mDismiss = true;
				}
				if (mDismiss) break;
			}
		} catch (IOException e) {

		}
	}

	// CONTESTANT METHODS

	private void formGroup(String contestant) {
		// log(mName + ": forming group");
		while (true) {
			synchronized (mRoom) {
				if (mRoom.areSeatsAvailable() && !mRoom.examInProgress()) {
					mRoom.fillSeat();
					waitForSignal(mRoom, mName + ": waiting to take exam");
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

	private void waitForResults() {
		synchronized (mConvey) {
			mExamOrder.add(this);

			waitForSignal(mConvey, "waiting for exam results");
		}

		if (!mPassedExam) {
			mDismiss = true;
		} else {
			mGame.addContestant(this);
		}
	}

	/**
	 * Wait to be introduced, then give my introduction and alert the Host am I done.
	 */
	private void waitForIntroduction() {
		synchronized (mConvey) {
			if (!mGiveIntro) waitForSignal(mConvey, null);
		}

		log(mName + ": " + readyQuotes[new Random().nextInt(readyQuotes.length)]);

		synchronized (intro) {
			intro.notify();
		}

	}

	/**
	 * While the game is in round play mode, wait for a question, think for some time, and then attempt to answer the question.
	 */
	private void roundPlay() {
		while (mGame.inRoundPlay()) {
			waitForQuestion();
			think();
			answerQuestion();
		}
	}

	/**
	 * Wait for the final round to begin and I am asked a question. If my {@link #mGameScore} is greater than 0, give a wager and attempt to answer.
	 * Otherwise, say goodbye.
	 */
	private void finalRound() {
		synchronized (mConvey) {
			waitForSignal(mConvey, null);
		}

		if (mGameScore <= 0) {
			log(mName + ": I did not bring my A game today.  See you next time!");
			mDismiss = true;
		} else {
			mFinalWager = new Random().nextInt(mGameScore + 1);
			think();
		}
		synchronized (answer) {
			answer.notify();
		}
	}

	private void waitForQuestion() {
		synchronized (question) {
			waitForSignal(question, null);
		}
	}

	private void think() {
		log(mName + ": thinking...");
		try {
			Thread.sleep(1500);
		} catch (InterruptedException e) {

		}
	}

	/**
	 * Attempt to answer the question and give the answer to the {@link Host} if first contestant. If I am the last contestant to answer, wake up the
	 * Host.
	 */
	private void answerQuestion() {
		synchronized (answer) {
			answer.incrementAnswers();

			if (answer.getAnsweredByThread() == null) answer.setAnsweredByThread(this);

			if (answer.everyoneAnswered()) {
				answer.notify();
			}
		}
	}

	// ANNOUNCER METHODS

	/**
	 * Give exams to {@link Contestant}s when the {@link Room} is full or when the final group is seated and not when another exam is in progress.
	 * When all test takers have taken the exam, no longer wait for others to take exam.
	 */
	private void giveExams() {
		while (true) {
			synchronized (mRoom) {
				if (mRoom.allTested()) break;
				if ((!mRoom.areSeatsAvailable() || mRoom.finalGroup()) && !mRoom.examInProgress()) {
					log(mName + ": group is ready");
					mRoom.notifyAll();
					mRoom.setExamInProgress(true);
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

		for (ServerThread c : mContestants) {
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
		Collections.sort(scores);
		Collections.reverse(scores);
		scores = scores.subList(0, 4);
		int spots = 4;

		for (ListIterator<ServerThread> it = mExamOrder.listIterator(); it.hasNext();) {
			ServerThread c = it.next();
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

		for (ServerThread c : mGame.getContestantsThreads()) {
			log(mName + ": Welcome " + c.getName() + " to the game.");
			synchronized (c.mConvey) {
				c.mGiveIntro = true;
				c.mConvey.notify();
			}

			synchronized (intro) {
				waitForSignal(intro, null);
			}

		}

		synchronized (mGame) {
			mGame.setGameStarted(true);
			mGame.notify();
		}
	}

	// HOST METHODS

	private void waitForGame2Start() {
		if (!mGame.isGameStarted()) {
			synchronized (mGame) {
				if (!mGame.isGameStarted()) {
					waitForSignal(mGame, null);
				}
			}
		}
	}

	private void roundPlayHost() {
		int numRounds = mGame.getNumRounds();
		int numQuestions = mGame.getNumQuestions();

		for (int i = 0; i < numRounds; i++) {
			log(mName + ": Starting Round " + (i + 1));

			for (int j = 0; j < numQuestions; j++) {
				if (i == (numRounds - 1) && j == (numQuestions - 1)) {
					synchronized (mGame) {
						mGame.setRoundPlay(false);
					}
				}
				askQuestion(j);
				waitForAnswer();
				validateAnswer();
			}
			log(mName + ": End of Round " + (i + 1));
			printScores();
		}
		log(mName + ": End of Round Play");
	}

	private void askQuestion(int questionNum) {
		log(mName + ": Asking question " + (questionNum + 1));

		synchronized (question) {
			answer.newAnswer();
			question.notifyAll();
		}
	}

	private void waitForAnswer() {
		while (true) {
			synchronized (answer) {
				if (!answer.everyoneAnswered()) {
					waitForSignal(answer, null);
				}
				break;
			}
		}
	}

	private void validateAnswer() {
		ServerThread c = answer.getAnsweredByThread();
		int ans = new Random().nextInt(101);
		boolean right = (ans * answer.getRightPercent()) > 35;
		log(mName + ": " + c.getName() + " has answered " + correct[right ? 1 : 0]);
		c.correctAnswer();
		log(mName + ": " + c.getName() + "'s score is now: " + c.getGameScore());
	}

	private void finalRoundHost() {
		for (ServerThread c : mGame.getContestantsThreads()) {
			log(mName + ": Asking " + c.getName() + " the final question");
			synchronized (c.mConvey) {
				c.mConvey.notify();
			}

			synchronized (answer) {
				if (c.getFinalWager() == -1) waitForSignal(answer, null);
			}
		}

		ServerThread top = null;
		for (ServerThread c : mGame.getContestantsThreads()) {
			if (c.getGameScore() <= 0) continue;
			if (top == null) top = c;

			boolean right = new Random().nextInt(101) > 50;

			if (right) {
				c.correctFinalAnswer(c.getFinalWager());
				if (c.getGameScore() > top.getGameScore()) top = c;
			} else {
				c.incorrectFinalAnswer(c.getFinalWager());
				if (c.getGameScore() > top.getGameScore()) top = c;
			}
		}

		printScores();
		log(mName + ": And the winner is...");
		log(mName + ": " + top.getName() + " with a score of " + top.getGameScore());
		log(mName + ": Thank you and drive home safely!");
	}

	private void printScores() {
		log(mName + ": Here are the scores");
		for (ServerThread c : mGame.getContestantsThreads()) {
			log(mName + ": " + c.getName() + ": " + c.getGameScore());
		}
	}

	public void setPassedExam(boolean passed) {
		mPassedExam = passed;
	}

	public void setExamScore(int score) {
		mExamScore = score;
	}

	public int getExamScore() {
		return mExamScore;
	}

	public String getName() {
		return mName;
	}

	public Integer getGameScore() {
		synchronized (mGameScore) {
			return mGameScore;
		}
	}

	public void correctAnswer() {
		synchronized (mGameScore) {
			mGameScore += mQuestionValues;
		}
	}

	public void wrongAnswer() {
		synchronized (mGameScore) {
			mGameScore -= mQuestionValues;
		}
	}

	public int getFinalWager() {
		return mFinalWager;
	}

	public void correctFinalAnswer(int wager) {
		synchronized (mGameScore) {
			mGameScore += wager;
		}
	}

	public void incorrectFinalAnswer(int wager) {
		synchronized (mGameScore) {
			mGameScore -= wager;
		}
	}

	/**
	 * Must be called within a synchronized block
	 * 
	 * @param obj
	 *            - Object to block on
	 */
	private void waitForSignal(Object obj, String comment) {
		if (comment != null) log(comment);
		while (true) {
			try {
				obj.wait();
				break;
			} catch (InterruptedException e) {
				continue;
			}
		}
	}

	private static String[]	correct		= { "incorrectly", "correctly" };

	private static String[]	readyQuotes	= { "Ready to play!", "Let's ROCK!", "I already won this game.", "I am awesome!", "Really? I made it?",
			"Where is my prize money?", "Thank you, thank you", "Let's drop the hammer", "Jean....can you hear me?", "I like to parrrr-taaayyy" };
}
