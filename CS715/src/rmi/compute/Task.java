package rmi.compute;

public interface Task<T> {
	T execute();
}