package rmi.tasks;

import java.io.Serializable;

import rmi.data.User;

public class GetSchedule implements Task<User>, Serializable {
	private static final long	serialVersionUID	= 4421061684153205530L;

	@Override
	public User execute() {
		return null;
	}

}
