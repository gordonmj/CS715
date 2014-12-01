package rmi.tasks;

import java.io.Serializable;
import java.util.Map;

import rmi.data.User;

public class GetSchedule implements Task<User>, Serializable {
	private static final long	serialVersionUID	= 4421061684153205530L;

	@Override
	public User execute() {
		return null;
	}

	@Override
	public void setUsers(Map<String, User> users) {
		// TODO Auto-generated method stub

	}

}
