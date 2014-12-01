package rmi.engine;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

import rmi.compute.Compute;
import rmi.data.User;
import rmi.tasks.Authenticate;
import rmi.tasks.Task;

public class EventEngine implements Compute {
	private static final int	PORT	= 24690;

	private List<User>			mUsers;

	public EventEngine() {
		super();
		mUsers = new ArrayList<User>();
		loadDefaultUsers();
	}

	public <T> T executeTask(Task<T> t) {
		if (t instanceof Authenticate) {
			Authenticate aTask = (Authenticate) t;
			aTask.setUsers(mUsers);
		}
		return t.execute();
	}

	private void loadDefaultUsers() {
		mUsers.add(new User("admin", "admin", User.Type.ADMIN));
	}

	public List<User> getUsers() {
		return mUsers;
	}

	public static void main(String[] args) {
		try {
			String name = "Event";
			Compute engine = new EventEngine();
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
