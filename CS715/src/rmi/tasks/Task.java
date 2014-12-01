package rmi.tasks;

import java.util.Map;

import rmi.data.User;

public interface Task<T> {
	public T execute();

	public void setUsers(Map<String, User> users);
}