package rmi.data;

import java.io.Serializable;

public class User implements Serializable {
	private static final long	serialVersionUID	= 3593705434962682247L;

	private String				mUsername			= "";
	private String				mPassword			= "";

	public User(String username, String password) {
		mUsername = username;
		mPassword = password;
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
}
