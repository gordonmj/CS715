package rmi.tasks;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

import rmi.data.Event;
import rmi.data.User;

public class DeleteEvent extends Task<Boolean> implements Serializable {
	private static final long	serialVersionUID	= 1469892477973061110L;
	private String				mUsername			= "";
	private int					mEventId			= 0;

	public DeleteEvent(String username, int eventId) {
		mUsername = username;
		mEventId = eventId;
	}

	public Boolean execute() {
		System.out.println("username: " + mUsername);
		User user = mUsers.get(mUsername);
		if (user != null) {
			List<Event> events = user.getEvents();

			for (Iterator<Event> iterator = events.iterator(); iterator.hasNext();) {
				Event event = iterator.next();

				if (event.getEventId() == mEventId) {
					iterator.remove();
					return true;
				}
			}
		}
		return false;
	}

}
