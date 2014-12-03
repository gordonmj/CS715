package rmi.tasks;

import java.io.Serializable;

import rmi.compute.Compute;
import rmi.data.User;
import rmi.data.Event;

import java.util.Date;
import java.util.List;

public class DeleteEvent extends Task<Boolean>  implements Serializable {	
	private static final long serialVersionUID = 1469892477973061110L;
	private Event				mEvent = null;
	private String				mUsername = "";
	private List <Event>		mEvents;
	public DeleteEvent(String username, int id){
		mUsername = username;
		User user = mUsers.get(mUsername);
		mEvents = user.getEvents();
		mEvent = mEvents.get(id);
	}
	
	public Boolean execute() {	
		mEvents.remove(mEvent);		
		return true;
	}
	
}
