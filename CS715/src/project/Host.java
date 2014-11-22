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
		log(mName + ": It's time for Final Guess What or Who?");
		finalRound();
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
				if (answer.getAnsweredBy() == null) {
					waitForSignal(answer, null);
				}
				break;
			}
		}
	}

	private void validateAnswer() {
		Contestant c = answer.getAnsweredBy();
		int ans = new Random().nextInt(101);
		boolean right = (ans * answer.getRightPercent()) > 35;
		log(mName + ": " + c.getName() + " has answered " + correct[right ? 1 : 0]);
		c.correctAnswer();
		log(mName + ": " + c.getName() + "'s score is now: " + c.getGameScore());
	}

	private void finalRound() {
		for (Contestant c : Contestant.mGame.getContestants()) {
			log(mName + ": asking " + c.getName() + " the final question");
			synchronized (c.mConvey) {
				c.mConvey.notify();
			}

			synchronized (answer) {
				if (c.getFinalWager() == -1) waitForSignal(answer, null);
			}
		}

		Contestant top = null;
		for (Contestant c : Contestant.mGame.getContestants()) {
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
		for (Contestant c : Contestant.mGame.getContestants()) {
			log(mName + ": " + c.getName() + ": " + c.getGameScore());
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

	private static String[]	correct	= { "incorrectly", "correctly" };
}