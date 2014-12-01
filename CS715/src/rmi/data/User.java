package rmi.data;

import java.io.Serializable;
import java.util.List;

public class User implements Serializable {
	private static final long	serialVersionUID	= 3593705434962682247L;
	public static final String	DEFAULT_PASSWORD	= "12345";

	private String				mUsername			= "";
	private String				mPassword			= "";
	private Type				mType				= Type.USER;			// default to USER
	private List<Event>			mEvents;

	public User(String username, String password, Type type) {
		mUsername = username;
		mPassword = password;
		mType = type;
	}

	public String getPassword() {
		return mPassword;
	}

	public void setPassword(String password) {
		mPassword = password;
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

	public static enum Type {
		USER, ADMIN
	}
}
