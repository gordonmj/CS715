package rmi.tasks;

public interface Task<T> {
	T execute();
}