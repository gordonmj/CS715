package rmi.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;

import rmi.data.Event;

public class User implements Serializable {
	private static final long	serialVersionUID	= 3593705434962682247L;
	public static final String	DEFAULT_PASSWORD	= "12345";

	private String				mUsername			= "";
	private String				mPassword			= "";
	private Type				mType				= Type.USER;				// default to USER
	private List<Event>			mEvents				= new ArrayList<Event>();
	private int					mNextEventID		= 1;

	public User(String username, String password, Type type) {
		mUsername = username;
		mPassword = password;
		mType = type;
		if (type != Type.ADMIN) {
			Date date = new Date();
			addEvent("Account created", date, getNextEventID());
		}
	}

	public String getPassword() {
		return mPassword;
	}

	public void setPassword(String password) {
		mPassword = password;
	}

	public int getNextEventID() {
		return mNextEventID++;
	}

	public void resetPassword() {
		mPassword = DEFAULT_PASSWORD;
	}

	public String getUsername() {
		return mUsername;
	}

	public void setUsername(String username) {
		mUsername = username;
	}

	public boolean isAdminAcct() {
		return mType == Type.ADMIN;
	}

	public List<Event> getEvents() {
		return mEvents;
	}

	public void removeEvent(Event e) {
		mEvents.remove(e);
	}

	public Event addEvent(String title, Date date, int eventId) {
		Event event = new Event(title, date, eventId);
		mEvents.add(event);
		return event;
	}

	public static enum Type {
		USER, ADMIN
	}
}
