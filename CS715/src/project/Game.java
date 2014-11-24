package project;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Game {
	private int					numRounds		= 2;
	private int					numQuestions	= 5;
	private List<Contestant>	mContestants	= Collections.synchronizedList(new ArrayList<Contestant>());
	private boolean				mRoundPlay		= true;
	private boolean				mGameStarted	= false;

	public synchronized void addContestant(Contestant c) {
		synchronized (mContestants) {
			mContestants.add(c);
		}
	}

	public synchronized List<Contestant> getContestants() {
		return mContestants;
	}

	public synchronized int getNumRounds() {
		return numRounds;
	}

	public synchronized void setNumRounds(int numRounds) {
		this.numRounds = numRounds;
	}

	public synchronized int getNumQuestions() {
		return numQuestions;
	}

	public synchronized void setNumQuestions(int numQuestions) {
		this.numQuestions = numQuestions;
	}

	public synchronized boolean inRoundPlay() {
		return mRoundPlay;
	}

	public synchronized void setRoundPlay(boolean roundPlay) {
		mRoundPlay = roundPlay;
	}

	public synchronized boolean isGameStarted() {
		return mGameStarted;
	}

	public synchronized void setGameStarted(boolean gameStarted) {
		mGameStarted = gameStarted;
	}
}