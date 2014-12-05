package rmi.tasks;

import java.io.Serializable;
import java.util.Date;

import rmi.data.Event;
import rmi.data.User;

public class AddEvent extends Task<Event> implements Serializable {
	private static final long	serialVersionUID	= 4537707372015945350L;
	private String				mUsername			= "";
	private String				mEventString		= "";
	private Date				mEventDate			= null;

	public AddEvent(String username, String title, Date date) {
		mUsername = username;
		mEventDate = date;
		mEventString = title;
	}

	public Event execute() {
		User user = mUsers.get(mUsername);
		if (user != null) {
			return user.addEvent(mEventString, mEventDate);
		}
		return null;
	}

}
