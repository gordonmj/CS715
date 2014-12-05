package rmi.tasks;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import rmi.data.Event;
import rmi.data.User;

public class EditEvent extends Task<Event> implements Serializable {
	private static final long	serialVersionUID	= 8181235707508864582L;
	private String				mEventString		= "";					// empty String means do not update
	private Date				mEventDate			= null;				// null Date means do no update
	private String				mUsername			= "";
	private int					mEventId			= 0;

	public EditEvent(String username, int eventId, String title, Date date) {
		mEventDate = date;
		mEventString = title;
		mUsername = username;
		mEventId = eventId;
	}

	public Event execute() {
		User user = mUsers.get(mUsername);
		if (user != null) {
			List<Event> events = user.getEvents();

			for (Event event : events) {
				if (event.getEventId() == mEventId) {
					if (mEventDate != null) {
						event.setDate(mEventDate);
					}
					if (!mEventString.isEmpty()) {
						event.setTitle(mEventString);
					}
					return event;
				}
			}
		}
		return null;
	}
}
