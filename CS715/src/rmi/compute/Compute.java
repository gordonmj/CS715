package rmi.compute;

import java.rmi.Remote;
import java.rmi.RemoteException;

import rmi.tasks.Task;

public interface Compute extends Remote {
	<T> T executeTask(Task<T> t) throws RemoteException;
}