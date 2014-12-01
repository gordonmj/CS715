package rmi.client;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

import rmi.compute.Compute;
import rmi.data.User;
import rmi.tasks.Authenticate;
import rmi.tasks.ResetPassword;
import rmi.tasks.CreateDelete;
import rmi.tasks.DisplayUserAccts;

public class Client implements Runnable {
	private static int		mPort		= 24690;					// default port
	private static Scanner	mScanner	= new Scanner(System.in);

	private User			mUser;
	private boolean			mExit		= false;

	public Client(int port) {
		mPort = port;
	}

	@Override
	public void run() {
		login();

		while (!mExit) {
			displayOptions();
			String option = getOption();
			performOption(option);
		}
	}

	private void performOption(String option) {
		if (mUser.isAdminAcct()) {
			switch (option) {
			case "1":
				// Create/Delete User Account
				createDeleteAcct();
				break;
			case "2":
				// Reset Password for User Account
				resetPassword(true);
				break;
			case "3":
				// Display List of User Account
				displayUsers();
				break;
			case "4":
				// Display schedule associated with specific user
				break;
			case "5":
				// Add an event to selected user’s schedule
				break;
			case "6":
				// Edit an event from selected user’s schedule
				break;
			case "7":
				// Delete an event from selected user’s schedule
				break;
			case "8":
				// Exit Program
				mExit = true;
				break;

			default:
				break;
			}
		} else {
			switch (option) {
			case "1":
				// Display user’s schedule
				break;
			case "2":
				// Add an event to user’s schedule
				break;
			case "3":
				// Edit an event from user’s schedule
				break;
			case "4":
				// Delete an event from user’s schedule
				break;
			case "5":
				// Change password to user’s account
				resetPassword(false);
				break;
			case "6":
				// Exit Program
				mExit = true;
				break;

			default:
				break;
			}
		}
	}

	private String getOption() {
		return mScanner.nextLine();
	}

	private void displayOptions() {
		if (mUser.isAdminAcct()) {
			System.out.println("(1) Create/Delete User Account (RMI item 2)");
			System.out.println("(2) Reset Password for User Account (RMI item 3) (admin may change password for all accounts)");
			System.out.println("(3) Display List of User Account (RMI item 4)");
			System.out.println("(4) Display schedule associated with specific user (RMI item 5)");
			System.out.println("(5) Add an event to selected user’s schedule (RMI item 6)");
			System.out.println("(6) Edit an event from selected user’s schedule (RMI item 7)");
			System.out.println("(7) Delete an event from selected user’s schedule (RMI item 8)");
			System.out.println("(8) Exit Program");
		} else {
			System.out.println("(1) Display user’s schedule (RMI item 5)");
			System.out.println("(2) Add an event to user’s schedule (RMI item 6)");
			System.out.println("(3) Edit an event from user’s schedule (RMI item 7)");
			System.out.println("(4) Delete an event from user’s schedule (RMI item 8)");
			System.out.println("(5) Change password to user’s account (RMI item 3) (user may only change his/her password)");
			System.out.println("(6) Exit Program");
		}
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
		String username = mScanner.nextLine();

		System.out.println("Enter password");
		String password = mScanner.nextLine();

		try {
			String name = "Event";
			Registry registry = LocateRegistry.getRegistry("localhost", mPort);
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

	private boolean createDeleteAcct() {
		boolean status = false;

		System.out.println("Create/Delete User Account");
		System.out.println("To delete account, leave password empty");
		System.out.println("Enter user name");
		String username = mScanner.nextLine();

		System.out.println("Enter password");
		String password = mScanner.nextLine();

		try {
			String name = "Event";
			Registry registry = LocateRegistry.getRegistry("localhost", mPort);
			Compute comp = (Compute) registry.lookup(name);
			CreateDelete task = new CreateDelete(username, password);
			status = comp.executeTask(task);
		} catch (Exception e) {
			System.err.println("Client exception:");
			e.printStackTrace();
		}

		if (password.isEmpty()) {
			// deleting account
			System.out.println("Deletion of " + username + (status ? " successful" : " unsuccessful"));
		} else {
			// creating account
			System.out.println("Creation of " + username + (status ? " successful" : " unsuccessful"));
		}

		return status;
	}

	private boolean resetPassword(boolean reset) {
		boolean status = false;
		System.out.println(reset ? "Reset Password for User Account" : "Change password to user’s account");
		String username = mUser.getUsername();

		if (reset) {// ask for username of account to reset password
			System.out.println("Enter user name");
			username = mScanner.nextLine();
		}

		String password = null;
		if (!reset) { // ask for new password
			System.out.println("Enter password");
			password = mScanner.nextLine();
		}

		try {
			String name = "Event";
			Registry registry = LocateRegistry.getRegistry("localhost", mPort);
			Compute comp = (Compute) registry.lookup(name);
			ResetPassword task = new ResetPassword(username, password);
			status = comp.executeTask(task);
		} catch (Exception e) {
			System.err.println("Client exception:");
			e.printStackTrace();
		}

		if (reset) {
			// resetting password
			System.out.println("Password reset" + (status ? " successfully" : " unsuccessfully"));
		} else {
			// changing password
			System.out.println("Password changed" + (status ? " successfully" : " unsuccessfully"));
		}
		return status;
	}

	private boolean displayUsers() {
		Map<String, User> users = null;
		System.out.println("Display List of User Account");

		try {
			String name = "Event";
			Registry registry = LocateRegistry.getRegistry("localhost", mPort);
			Compute comp = (Compute) registry.lookup(name);
			DisplayUserAccts task = new DisplayUserAccts();
			users = comp.executeTask(task);
		} catch (Exception e) {
			System.err.println("Client exception:");
			e.printStackTrace();
		}

		if (users == null) {
			System.out.println("Error retrieving users list");
		} else {
			System.out.println("Users in the system:");
			for (Entry<String, User> entry : users.entrySet()) {
				System.out.println(entry.getValue().getUsername());
			}
		}
		return users == null;
	}
}
