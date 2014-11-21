package project;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Contestant extends Logger implements Runnable {
	public static volatile List<Object>		mExamOrder			= Collections.synchronizedList(new ArrayList<Object>());
	public static volatile List<Contestant>	mContestants		= Collections.synchronizedList(new ArrayList<Contestant>());
	public static boolean					roundPlay			= true;
	public static Room						mRoom				= new Room();
	public static Game						mGame				= new Game();

	private static int						numContestants		= -1;
	private static long						examTime			= 5000L;

	private static int						numAnswers			= 0;
	private static int						numContestantsReady	= 0;

	private String							mName;
	private int								mExamScore;
	private boolean							mPassedExam			= false;
	private Integer							mGameScore			= 0;

	public Contestant(String name, int num_contestants) {
		mName = "Contestant" + name;

		synchronized (mRoom) {
			if (numContestants == -1) {
				numContestants = num_contestants;
				mRoom.setRoomCapacity(4);
				mRoom.setTotalExams2Give(num_contestants);
			}
		}
	}

	@Override
	public void run() {
		formGroup();
		takeExam();
		waitForResults();
		waitForGame();
		// roundPlay();
	}

	private void formGroup() {
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
			this.wait(examTime);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		synchronized (mRoom) {
			mRoom.examTaken();
		}
	}

	private void waitForResults() {
		Object convey = new Object();

		synchronized (convey) {
			mExamOrder.add(convey);
			mContestants.add(this);

			waitForSignal(convey, "waiting for exam results");
		}

		if (!mPassedExam) {
			try {
				Thread.currentThread().join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private void waitForGame() {
		synchronized (mGame) {
			waitForSignal(mGame, "waiting for game to start");
		}

		log(mName + ": " + readyQuotes[new Random().nextInt(readyQuotes.length)]);
		Logger.print();
	}

	private void roundPlay() {
		while (roundPlay) {
			waitForQuestion();
			think();
			answerQuestion();
		}
	}

	private void waitForQuestion() {
		synchronized (Host.askQuestion) {
			numContestantsReady++;

			if (numContestantsReady == 4) {
				numContestantsReady = 0;
				Host.askQuestion.notify();
			}
		}

		synchronized (Host.question) {
			if (!Host.questionAsked) waitForSignal(Host.question, "waiting for question");
		}
	}

	private void think() {
		log(mName + ": thinking...");
		synchronized (this) {
			try {
				this.wait(new Random().nextInt(4) * 500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private void answerQuestion() {
		synchronized (Host.answer) {
			if (Host.answeredBy == null) {
				Host.answeredBy = this;
			}
			numAnswers++;
			if (numAnswers == 4) Host.answer.notify();
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
			mGameScore += 200;
		}
	}

	public void wrongAnswer() {
		synchronized (mGameScore) {
			mGameScore -= 200;
		}
	}

	/**
	 * Must be called within a synchronized block
	 * 
	 * @param obj
	 *            - Object to block on
	 */
	private void waitForSignal(Object obj, String comment) {
		if (comment != null) log(mName + ": " + comment);
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
			"Where is my prize money?", "Thank you, thank you", "Let's drop the hammer" };
}