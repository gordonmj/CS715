package rmi.data;

import java.io.Serializable;

public class User implements Serializable {
	private static final long	serialVersionUID	= 3593705434962682247L;

	private String				mUsername			= "";
	private String				mPassword			= "";
	private Type				mType;

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

	public String getUsername() {
		return mUsername;
	}

	public void setUsername(String username) {
		mUsername = username;
	}

	public boolean isAdminAcct() {
		return mType == Type.ADMIN;
	}

	public static enum Type {
		USER, ADMIN
	}
}
