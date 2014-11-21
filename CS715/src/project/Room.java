package project;

public class Room {
	private int		mRoomCapacity;
	private int		mSeatsAvailable;
	private int		mTotalExams2Give;
	private int		mNumExamsTaken	= 0;
	private boolean	mExamInProgress	= false;

	public Room() {
	}

	public synchronized void setTotalExams2Give(int totalExams2Give) {
		mTotalExams2Give = totalExams2Give;
	}

	public synchronized void setRoomCapacity(int roomCap) {
		mRoomCapacity = roomCap;
		mSeatsAvailable = mRoomCapacity;
	}

	public synchronized int getRoomCapacity() {
		return mRoomCapacity;
	}

	public synchronized void emptyRoom() {
		mSeatsAvailable = mRoomCapacity;
	}

	public synchronized void fillSeat() {
		mSeatsAvailable--;
	}

	public synchronized boolean areSeatsAvailable() {
		return mSeatsAvailable > 0;
	}

	public synchronized void examTaken() {
		mNumExamsTaken++;
		mSeatsAvailable++;

		if (mSeatsAvailable == mRoomCapacity) mExamInProgress = false;
	}

	public synchronized boolean examInProgress() {
		return mExamInProgress;
	}

	public synchronized void setExamInProgress(boolean inProgress) {
		mExamInProgress = inProgress;
	}

	public synchronized boolean allTested() {
		return mNumExamsTaken == mTotalExams2Give;
	}

	public synchronized boolean finalGroup() {
		int seatsFilled = mRoomCapacity - mSeatsAvailable;
		return mTotalExams2Give - (mNumExamsTaken + seatsFilled) == 0;
	}
}