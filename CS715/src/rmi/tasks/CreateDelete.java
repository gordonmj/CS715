package rmi.tasks;

import java.io.Serializable;
import java.util.Date;

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
				mUsers.remove(mUsername);
				status = true;
			}
		} else {
			// creating account
			User user1 = new User(mUsername, mPassword, User.Type.USER);
			mUsers.put(mUsername, user1);
//			user1.addEvent("Account created", new Date());
			status = true;
		}
		return status;
	}

}
