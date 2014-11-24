package project;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Contestant extends Logger implements Runnable {
	public static List<Contestant>	mContestants	= Collections.synchronizedList(new ArrayList<Contestant>());
	public static Room				mRoom			= new Room();
	public static Game				mGame			= new Game();

	public Object					mConvey			= new Object();

	private static int				mNumContestants	= -1;
	private static long				mExamTime		= 5000L;
	private static int				mQuestionValues	= 200;

	private String					mName;
	private int						mExamScore;
	private boolean					mPassedExam		= false;
	private Integer					mGameScore		= 0;
	private int						mFinalWager		= -1;
	private boolean					mDismiss		= false;

	public Contestant(String name, int num_contestants, int questionValues, int numRounds, int numQuestions, int room_capacity) {
		mName = "Contestant" + name;

		synchronized (mRoom) {
			if (mNumContestants == -1) { // first contestant needs to set parameters
				mNumContestants = num_contestants;
				mRoom.setRoomCapacity(room_capacity);
				mRoom.setTotalExams2Give(num_contestants);
				mGame.setNumQuestions(numQuestions);
				mGame.setNumRounds(numRounds);
				mQuestionValues = questionValues;
			}
		}
	}

	@Override
	public void run() {
		formGroup();
		takeExam();
		waitForResults();
		if (!mDismiss) waitForIntroduction();
		if (!mDismiss) roundPlay();
		if (!mDismiss) finalRound();
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
			this.wait(mExamTime);
		} catch (InterruptedException e) {

		}
		synchronized (mRoom) {
			mRoom.examTaken();
		}
	}

	private void waitForResults() {
		synchronized (mConvey) {
			mContestants.add(this);

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
			waitForSignal(mConvey, null);
		}

		log(mName + ": " + readyQuotes[new Random().nextInt(readyQuotes.length)]);

		synchronized (Announcer.intro) {
			Announcer.intro.notify();
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
		synchronized (Host.answer) {
			Host.answer.notify();
		}
	}

	private void waitForQuestion() {
		synchronized (Host.question) {
			waitForSignal(Host.question, null);
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
		synchronized (Host.answer) {
			Host.answer.incrementAnswers();

			if (Host.answer.getAnsweredBy() == null) Host.answer.setAnsweredBy(this);

			if (Host.answer.everyoneAnswered()) {
				Host.answer.notify();
			}
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
			"Where is my prize money?", "Thank you, thank you", "Let's drop the hammer", "Jean....can you hear me?", "I like to parrrr-taaayyy" };
}