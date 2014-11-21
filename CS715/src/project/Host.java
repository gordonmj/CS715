package project;

import java.util.Random;

public class Host extends Logger implements Runnable {
	private String			mName		= "Host";

	public static Answer	answer		= new Answer();
	public static Object	question	= new Object();

	@Override
	public void run() {

		if (!Contestant.mGame.isGameStarted()) {
			synchronized (Contestant.mGame) {
				if (!Contestant.mGame.isGameStarted()) {
					waitForSignal(Contestant.mGame, null);
				}
			}
		}
		roundPlay();
		Logger.print();
	}

	private void roundPlay() {
		int numRounds = Contestant.mGame.getNumRounds();
		int numQuestions = Contestant.mGame.getNumQuestions();

		for (int i = 0; i < numRounds; i++) {
			log(mName + ": Starting Round " + (i + 1));

			for (int j = 0; j < numQuestions; j++) {
				if (i == (numRounds - 1) && j == (numQuestions - 1)) {
					synchronized (Contestant.mGame) {
						Contestant.mGame.setRoundPlay(false);
					}
				}
				askQuestion(j);
				waitForAnswer();
				validateAnswer();
			}
			log(mName + ": End of Round " + (i + 1));
			log(mName + ": Here are the scores");

			for (Contestant c : Contestant.mGame.getContestants()) {
				log(mName + ": " + c.getName() + ": " + c.getGameScore());
			}
			Logger.print();
		}
		log(mName + ": End of Round Play");
	}

	private void askQuestion(int questionNum) {
		log(mName + ": Asking question " + (questionNum + 1));

		synchronized (question) {
			answer.newAnswer();
			question.notifyAll();
		}
		Logger.print();
	}

	private void waitForAnswer() {
		Logger.print();
		while (true) {
			synchronized (answer) {
				if (answer.getAnsweredBy() == null) {
					waitForSignal(answer, null);
				}
				break;
			}
		}
		Logger.print();
	}

	private void validateAnswer() {
		Contestant c = answer.getAnsweredBy();
		int ans = new Random().nextInt(101);
		boolean right = (ans * answer.getRightPercent()) > 35;
		log(mName + ": " + c.getName() + " has answered " + correct[right ? 1 : 0]);
		c.correctAnswer();
		log(mName + ": " + c.getName() + "'s score is now: " + c.getGameScore());
		Logger.print();
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

	private static String[]	correct	= { "incorrectly", "correctly" };
}