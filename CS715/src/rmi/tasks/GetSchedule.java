package rmi.tasks;

import java.io.Serializable;
import java.util.List;

import rmi.data.Event;
import rmi.data.User;

public class GetSchedule extends Task<List<Event>> implements Serializable {
	private static final long	serialVersionUID	= 4421061684153205530L;
	private String				mUsername			= "";

	public GetSchedule(String username) {
		mUsername = username;
	}

	public List<Event> execute() {
		User user = mUsers.get(mUsername);
		if (user != null) {
			return user.getEvents();
		} else return null;
	}

}
