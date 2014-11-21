package project;

public class Host extends Logger implements Runnable {
	private String				mName			= "Host";

	public static Object		beginGame		= new Object();
	public static Object		answer			= new Object();
	public static Object		askQuestion		= new Object();
	public static Object		question		= new Object();

	public static Contestant	answeredBy		= null;
	public static boolean		answerPossible	= true;
	public static boolean		gameStarted		= false;
	public static boolean		questionAsked	= false;

	@Override
	public void run() {
		/*
		synchronized (beginGame) {
			if (!gameStarted) waitForSignal(beginGame, "waiting for game to begin");
		}

		roundPlay();
		*/
		Logger.print();
	}

	private void roundPlay() {
		int numRounds = Contestant.mGame.getNumRounds();
		int numQuestions = Contestant.mGame.getNumQuestions();

		for (int i = 0; i < numRounds; i++) {
			log(mName + ": Starting Round " + (i + 1));

			for (int j = 0; j < numQuestions; j++) {
				if (i == (numRounds - 1) && j == (numRounds - 1)) Contestant.roundPlay = false;
				Logger.print();
				waitForContestants();
				Logger.print();
				askQuestion(j);
				Logger.print();
				waitForAnswer();
				Logger.print();
				validateAnswer();
				Logger.print();
			}
		}
	}

	private void waitForContestants() {
		synchronized (askQuestion) {
			waitForSignal(askQuestion, "waiting to ask a question");
		}
	}

	private void askQuestion(int questionNum) {
		log(mName + ": Asking question " + (questionNum + 1));
		answeredBy = null;
		synchronized (question) {
			questionAsked = true;
			question.notifyAll();
		}
	}

	private void waitForAnswer() {
		synchronized (answer) {
			waitForSignal(answer, "waiting for answer");
		}
	}

	private void validateAnswer() {

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

	static class Question {
		private int	contestantsWaiting	= 0;

		public synchronized boolean areContestantsReady() {
			return this.contestantsWaiting == 4;
		}

		public synchronized void contestantReady() {
			this.contestantsWaiting++;
		}

		public synchronized void reset() {
			this.contestantsWaiting = 0;
		}
	}
}