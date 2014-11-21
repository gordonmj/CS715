package project;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;

public class Announcer extends Logger implements Runnable {
	private List<Contestant>	mContestants;
	private String				mName	= "Announcer";

	public Announcer(List<Contestant> contestants) {
		mContestants = contestants;
	}

	@Override
	public void run() {
		log(mName + ": executing");
		while (!Contestant.allExamsHandedOut) {
			synchronized (Contestant.mRoom) {
				if (!Contestant.mRoom.areSeatsAvailable() || Contestant.allExamsHandedOut) {
					log(mName + ": group is ready");
					Logger.print();
					Contestant.mRoom.notifyAll();
					Contestant.mRoom.emptyRoom();
				}
			}
		}

		while (!Contestant.allTested) {
			synchronized (Contestant.allTested) {
				if (Contestant.allTested) break;
			}
		}

		List<Integer> scores = gradeExams();
		announceScores(scores);
		// startGame();
	}

	private List<Integer> gradeExams() {
		log(mName + ": grading exams");
		Logger.print();
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
		int index = 0;
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
			Object convey = Contestant.mExamOrder.get(index);
			synchronized (convey) {
				convey.notify();
			}
			index++;
		}
		Logger.print();
	}

	private void startGame() {
		log(mName + ": The game will now start");

		new Thread(new Host()).start();

		for (Contestant c : Contestant.mContestants) {
			log(mName + ": Welcome " + c.getName() + " to the game.");
		}

		synchronized (Host.beginGame) {
			Host.beginGame.notify();
			Host.gameStarted = true;
		}

		synchronized (Contestant.mGame) {
			Contestant.mGame.notifyAll();
		}

		Logger.print();
		try {
			Thread.currentThread().join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}