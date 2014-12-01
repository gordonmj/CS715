package rmi.engine;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;

import rmi.compute.Compute;
import rmi.data.User;
import rmi.tasks.Task;

public class EventEngine implements Compute, Runnable {
	private static final int	PORT	= 24690;	// default port

	private Map<String, User>	mUsers;

	public EventEngine() {
		super();
		mUsers = new HashMap<String, User>();
		loadDefaultUsers();
	}

	public <T> T executeTask(Task<T> t) {
		t.setUsers(mUsers);
		return t.execute();
	}

	private void loadDefaultUsers() {
		mUsers.put("admin", new User("admin", "admin", User.Type.ADMIN));
	}

	public Map<String, User> getUsers() {
		return mUsers;
	}

	@Override
	public void run() {
		try {
			String name = "Event";
			// Compute engine = new EventEngine(mPort);
			Compute stub = (Compute) UnicastRemoteObject.exportObject(this, 0);
			Registry registry = LocateRegistry.createRegistry(PORT);
			registry.rebind(name, stub);
			System.out.println("EventEngine bound");
		} catch (Exception e) {
			System.err.println("EventEngine exception:");
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		new Thread(new EventEngine()).start();
	}
}
