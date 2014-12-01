package rmi.tasks;

import java.io.Serializable;
import java.util.Map;

import rmi.data.User;

public class DisplayUserAccts implements Task<User>, Serializable {
	private static final long	serialVersionUID	= -1263998019923181454L;

	@Override
	public User execute() {
		return null;
	}

	@Override
	public void setUsers(Map<String, User> users) {
		// TODO Auto-generated method stub

	}

}
