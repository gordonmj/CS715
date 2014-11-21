package project;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Game {
	private int					numRounds		= 2;
	private int					numQuestions	= 5;
	private List<Contestant>	mContestants	= Collections.synchronizedList(new ArrayList<Contestant>());

	public synchronized void addContestant(Contestant c) {
		mContestants.add(c);
	}

	public List<Contestant> getContestants() {
		return mContestants;
	}

	public int getNumRounds() {
		return numRounds;
	}

	public void setNumRounds(int numRounds) {
		this.numRounds = numRounds;
	}

	public int getNumQuestions() {
		return numQuestions;
	}

	public void setNumQuestions(int numQuestions) {
		this.numQuestions = numQuestions;
	}
}