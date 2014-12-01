package rmi.tasks;

import java.io.Serializable;
import java.util.Map;

import rmi.data.User;

public class CreateDelete implements Task<Boolean>, Serializable {
	private static final long	serialVersionUID	= -6037436659887314867L;

	private String				mUsername;
	private String				mPassword;
	private Map<String, User>	mUsers;

	public CreateDelete(String username, String password) {
		mUsername = username;
		mPassword = password;
	}

	@Override
	public Boolean execute() {
		boolean status = false;
		User user = mUsers.get(mUsername);

		if (mPassword.isEmpty()) {
			// deleting account
			if (user != null) {
				mUsers.remove(mUsername);
				status = true;
			}
		} else {
			// creating account
			mUsers.put(mUsername, new User(mUsername, mPassword, User.Type.USER));
			status = true;
		}
		return status;
	}

	public void setUsers(Map<String, User> users) {
		mUsers = users;
	}
}
