package rmi.tasks;

import java.io.Serializable;
import java.util.Map;

import rmi.data.User;

public class ResetPassword extends Task<Boolean> implements Serializable {
	private static final long	serialVersionUID	= -1113974153102066593L;

	private String				mUsername;
	private String				mPassword;
	private Map<String, User>	mUsers;

	public ResetPassword(String username, String password) {
		mUsername = username;
		mPassword = password;
	}

	public Boolean execute() {
		boolean status = false;
		User user = mUsers.get(mUsername);

		if (user != null) {
			if (mPassword == null) {
				user.resetPassword();
			} else {
				user.setPassword(mPassword);
			}
			status = true;
		}

		return status;
	}

}
