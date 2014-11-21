package project;

public class Room {
	private static final int	ROOM_CAPACITY	= 4;
	private static int			mSeatsAvailable	= ROOM_CAPACITY;

	public synchronized int getRoomCapacity() {
		return ROOM_CAPACITY;
	}

	public synchronized void emptyRoom() {
		Room.mSeatsAvailable = ROOM_CAPACITY;
	}

	public synchronized void fillSeat() {
		Room.mSeatsAvailable--;
	}

	public synchronized boolean areSeatsAvailable() {
		return mSeatsAvailable > 0;
	}
}