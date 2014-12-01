package rmi.tasks;

import java.io.Serializable;
import java.util.List;

import rmi.data.User;

public class Authenticate implements Task<User>, Serializable {
	private static final long	serialVersionUID	= -8375785893425015013L;

	private String				mUsername;
	private String				mPassword;
	private List<User>			mUsers;

	public Authenticate(String username, String pwd) {
		mUsername = username;
		mPassword = pwd;
	}

	@Override
	public User execute() {
		User u = null;
		for (User user : mUsers) {
			if (user.getUsername().equals(mUsername) && user.getPassword().equals(mPassword)) {
				u = user;
				break;
			}
		}
		return u;
	}

	public void setUsers(List<User> users) {
		mUsers = users;
	}
}
