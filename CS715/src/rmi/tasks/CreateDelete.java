package rmi.tasks;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

import rmi.data.User;

public class CreateDelete implements Task<Boolean>, Serializable {
	private static final long	serialVersionUID	= -6037436659887314867L;

	private String				mUsername;
	private String				mPassword;
	private List<User>			mUsers;

	public CreateDelete(String username, String password) {
		mUsername = username;
		mPassword = password;
	}

	@Override
	public Boolean execute() {
		boolean status = false;
		if (mPassword.isEmpty()) {
			// deleting account
			for (Iterator<User> iter = mUsers.iterator(); iter.hasNext();) {
				User user = iter.next();
				if (user.getUsername().equals(mUsername)) {
					iter.remove();
					status = true;
					break;
				}
			}
		} else {
			// creating account
			status = mUsers.add(new User(mUsername, mPassword, User.Type.USER));
		}
		return status;
	}

	public void setUsers(List<User> users) {
		mUsers = users;
	}
}
