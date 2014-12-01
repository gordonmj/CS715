package rmi.tasks;

public interface Task<T> {
	public T execute();
}