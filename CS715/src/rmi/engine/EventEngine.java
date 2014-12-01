package rmi.engine;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import rmi.compute.Compute;
import rmi.tasks.Task;

public class EventEngine implements Compute {
	private static final int	PORT	= 24680;

	public EventEngine() {
		super();
	}

	public <T> T executeTask(Task<T> t) {
		return t.execute();
	}

	public static void main(String[] args) {
		try {
			String name = "Event";
			Compute engine = new ComputeEngine();
			Compute stub = (Compute) UnicastRemoteObject.exportObject(engine, 0);
			Registry registry = LocateRegistry.createRegistry(PORT);
			registry.rebind(name, stub);
			System.out.println("EventEngine bound");
		} catch (Exception e) {
			System.err.println("EventEngine exception:");
			e.printStackTrace();
		}
	}
}
