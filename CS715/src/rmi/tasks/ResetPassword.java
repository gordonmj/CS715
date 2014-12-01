package rmi.tasks;

import java.io.Serializable;

import rmi.data.User;

public class ResetPassword implements Task<User>, Serializable {
	private static final long	serialVersionUID	= -1113974153102066593L;

	@Override
	public User execute() {
		return null;
	}

}
