package project;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;

public class Announcer extends Logger implements Runnable {
	private List<Contestant>	mContestants;
	private String				mName	= "Announcer";

	public static Object		intro	= new Object();

	public Announcer(List<Contestant> contestants) {
		mContestants = contestants;
	}

	@Override
	public void run() {
		log(mName + ": executing");
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

		List<Integer> scores = gradeExams();
		announceScores(scores);
		startGame();
	}

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

	private void announceScores(List<Integer> scores) {
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

	private void startGame() {
		log(mName + ": print an opening message (something useful, it is up to you)");
		new Thread(new Host()).start();

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
}