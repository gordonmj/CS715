package rmi.client;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

import rmi.compute.Compute;
import rmi.data.User;
import rmi.tasks.Authenticate;

public class Client implements Runnable {
	private static final int	PORT		= 24690;
	private static Scanner		mConsole	= new Scanner(System.in);

	private User				mUser;

	@Override
	public void run() {

		while (mUser == null) {
			authenticate();
		}

	}

	private boolean authenticate() {
		boolean status = false;

		// 1. Client program will prompt user to enter user name and password.
		System.out.println("Enter user name");
		String username = mConsole.nextLine();

		System.out.println("Enter password");
		String password = mConsole.nextLine();

		try {
			String name = "Event";
			Registry registry = LocateRegistry.getRegistry("localhost", PORT);
			Compute comp = (Compute) registry.lookup(name);
			Authenticate task = new Authenticate(username, password);
			mUser = comp.executeTask(task);
			status = mUser != null;
		} catch (Exception e) {
			System.err.println("Client exception:");
			e.printStackTrace();
		}
		return status;
	}
}
