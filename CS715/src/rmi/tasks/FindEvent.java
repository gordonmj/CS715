package rmi.tasks;

import java.io.Serializable;

import rmi.compute.Compute;
import rmi.data.User;
import rmi.data.Event;

import java.util.Date;
import java.util.List;

public class FindEvent extends Task<Event> implements Serializable {	
	private static final long serialVersionUID = -5650631568386141268L;
	private String				mUsername = "";
	private String				mEventTitle = "";
	
	public FindEvent(String username, String title){
		mUsername = username;
		mEventTitle = title;
		
	}
	
	public Event execute() {	
		User user = mUsers.get(mUsername);
		List <Event> events = user.getEvents();
		for (int i=0; i < events.size(); i++){
			System.out.println("i: "+i+", event i: "+events.get(i).getTitle()+" and search title is: "+mEventTitle);
			if (events.get(i).getTitle() == mEventTitle){
				System.out.println("EQUAL!");
				return events.get(i);
			}
		}
		return null;
	}
	
}
