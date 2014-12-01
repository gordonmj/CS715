package rmi.tasks;

import java.io.Serializable;

import rmi.data.User;

public class GetSchedule extends Task<User> implements Serializable {
	private static final long	serialVersionUID	= 4421061684153205530L;

	public User execute() {
		return null;
	}

}
