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
		login();
	}

	/**
	 * <ol>
	 * <li>Client program will prompt user to enter user name and password.</li>
	 * <li>Client program will then use RMI (item 1) to identify if this user name/password combination is correct.</li>
	 * <ul>
	 * <li>If user name/password combination is NOT correct, display invalid credentials on the client screen and exit.</li>
	 * <li>If user name/password combination is correct, display welcome User or welcome Administrator based on user account type.</li>
	 * </ul>
	 * </ol>
	 */
	private void login() {
		while (mUser == null) {
			if (!authenticate()) {
				System.out.println("Invalid credentials");
			}
		}
		System.out.println("Welcome " + (mUser.isAdminAcct() ? "Administrator" : "User"));
	}

	private boolean authenticate() {
		boolean status = false;

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
