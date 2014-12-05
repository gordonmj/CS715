package rmi.tasks;

import java.io.Serializable;
import java.util.List;

import rmi.data.Event;
import rmi.data.User;

public class FindEvent extends Task<Event> implements Serializable {
	private static final long	serialVersionUID	= -5650631568386141268L;
	private String				mUsername			= "";
	private String				mEventTitle			= "";

	public FindEvent(String username, String title) {
		mUsername = username;
		mEventTitle = title;

	}

	public Event execute() {
		User user = mUsers.get(mUsername);
		if (user != null) {
			List<Event> events = user.getEvents();

			for (Event event : events) {
				if (event.getTitle().equalsIgnoreCase(mEventTitle)) {
					return event;
				}
			}
		}
		return null;
	}

}
