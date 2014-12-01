package rmi.tasks;

import java.io.Serializable;
import java.util.Map;

import rmi.data.User;

public class DisplayUserAccts extends Task<Map<String, User>> implements Serializable {
	private static final long	serialVersionUID	= -1263998019923181454L;

	public Map<String, User> execute() {
		return mUsers;
	}

}
