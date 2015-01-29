package project1;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import sockets.server.ServerThread;

public class Game {
	private int					numRounds			= 2;
	private int					numQuestions		= 5;
	private List<Contestant>	mContestants		= Collections.synchronizedList(new ArrayList<Contestant>());
	private List<ServerThread>	mContestantsThreads	= Collections.synchronizedList(new ArrayList<ServerThread>());
	private boolean				mRoundPlay			= true;
	private boolean				mGameStarted		= false;

	public synchronized void addContestant(Contestant c) {
		synchronized (mContestants) {
			mContestants.add(c);
		}
	}

	public synchronized List<Contestant> getContestants() {
		return mContestants;
	}

	public void addContestant(ServerThread c) {
		synchronized (mContestantsThreads) {
			mContestantsThreads.add(c);
		}
	}

	public synchronized List<ServerThread> getContestantsThreads() {
		return mContestantsThreads;
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