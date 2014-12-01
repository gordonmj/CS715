package rmi.tasks;

import java.io.Serializable;
import java.util.Map;

import rmi.data.User;

public class ChangePassword implements Task<Boolean>, Serializable {
	private static final long	serialVersionUID	= -1113974153102066593L;

	private String				mUsername;
	private String				mPassword;
	private Map<String, User>	mUsers;

	public ChangePassword(String username, String password) {
		mUsername = username;
		mPassword = password;
	}

	@Override
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

	public void setUsers(Map<String, User> users) {
		mUsers = users;
	}
}
