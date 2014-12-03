package rmi.tasks;

import java.io.Serializable;

import rmi.compute.Compute;
import rmi.data.User;
import rmi.data.Event;

import java.util.Date;
import java.util.List;

public class EditEvent extends Task<Event> implements Serializable {	
	private static final long serialVersionUID = 8181235707508864582L;
	private String				mEventString = "";
	private Date				mEventDate = null;
	private Event				mEvent = null;
	private String				mUsername = "";
	private int					mId = 0;
	
	public EditEvent(Event event, String title, Date date){
		mEventDate = date;
		mEventString = title;
		mEvent = event;
		
	}
	
	public EditEvent(String username, int id, String title, Date date){
		mEventDate = date;
		mEventString = title;
		mUsername = username;
		mId = id;
	}
	
	public Event execute() {	
		System.out.println("username: "+mUsername);
		User user = mUsers.get(mUsername);
		List <Event> events = user.getEvents();
		mEvent = events.get(mId);
		if (mEventDate != null){
			mEvent.setDate(mEventDate);
		}
		if (!mEventString.equals("")){
			mEvent.setTitle(mEventString);
		}
		
		return mEvent;
	}
	
}
