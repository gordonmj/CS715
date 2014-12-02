package rmi.tasks;

import java.util.Map;

import rmi.data.User;

public abstract class Task<T> {
	protected Map<String, User>	mUsers;

	public abstract T execute();

	
	public void setUsers(Map<String, User> users) {
		mUsers = users;
	}
}