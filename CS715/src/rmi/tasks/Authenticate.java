package rmi.tasks;

import java.io.Serializable;
import java.util.Map;

import rmi.data.User;

public class Authenticate implements Task<User>, Serializable {
	private static final long	serialVersionUID	= -8375785893425015013L;

	private String				mUsername;
	private String				mPassword;
	private Map<String, User>	mUsers;

	public Authenticate(String username, String password) {
		mUsername = username;
		mPassword = password;
	}

	@Override
	public User execute() {
		User user = mUsers.get(mUsername);

		if (user != null) {
			if (user.getUsername().equals(mUsername) && user.getPassword().equals(mPassword)) {
				return user;
			}
		}
		return null;
	}

	public void setUsers(Map<String, User> users) {
		mUsers = users;
	}
}
