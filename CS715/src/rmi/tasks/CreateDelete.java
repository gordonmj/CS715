package rmi.tasks;

import java.io.Serializable;

import rmi.data.User;

public class CreateDelete extends Task<Boolean> implements Serializable {
	private static final long	serialVersionUID	= -6037436659887314867L;

	private String				mUsername;
	private String				mPassword;

	public CreateDelete(String username, String password) {
		mUsername = username;
		mPassword = password;
	}

	public Boolean execute() {
		boolean status = false;
		User user = mUsers.get(mUsername);

		if (mPassword.isEmpty()) {
			// deleting account
			if (user != null) {
				status = mUsers.remove(mUsername) == null ? false : true;
			}
		} else {
			// creating account
			User user1 = new User(mUsername, mPassword, User.Type.USER);
			status = mUsers.put(mUsername, user1) == null ? false : true;
		}
		return status;
	}

}
