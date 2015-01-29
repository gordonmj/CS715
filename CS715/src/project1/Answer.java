package project1;

import sockets.server.ServerThread;

public class Answer {

	private int				mNumAnswers		= 0;
	private Contestant		mAnsweredBy;
	private ServerThread	mAnsweredByThread;
	private double			mRightPercent	= 0.65;

	public synchronized void newAnswer() {
		mNumAnswers = 0;
		mAnsweredBy = null;
	}

	public synchronized int getNumAnswers() {
		return mNumAnswers;
	}

	public synchronized void incrementAnswers() {
		mNumAnswers++;
	}

	public synchronized Contestant getAnsweredBy() {
		return mAnsweredBy;
	}

	public synchronized void setAnsweredBy(Contestant answeredBy) {
		mAnsweredBy = answeredBy;
	}

	public synchronized ServerThread getAnsweredByThread() {
		return mAnsweredByThread;
	}

	public synchronized void setAnsweredByThread(ServerThread answeredBy) {
		mAnsweredByThread = answeredBy;
	}

	public synchronized double getRightPercent() {
		return mRightPercent;
	}

	public synchronized void setRightPercent(double rightPercent) {
		mRightPercent = rightPercent;
	}

	public synchronized boolean everyoneAnswered() {
		return mNumAnswers == 4;
	}
}
