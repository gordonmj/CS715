package rmi.tasks;

import java.io.Serializable;

import rmi.compute.Compute;
import rmi.data.User;
import rmi.data.Event;

import java.util.List;

public class GetSchedule extends Task<List<Event>> implements Serializable {
	private static final long	serialVersionUID	= 4421061684153205530L;
	private String				mUsername = "";

	public GetSchedule(String username){
		mUsername = username;
	}
	
	public List<Event> execute() {	
		User user = mUsers.get(mUsername);
		return user.getEvents();
	}
	
}
