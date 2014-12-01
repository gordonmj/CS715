package rmi.client;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

import rmi.compute.Compute;
import rmi.data.User;
import rmi.tasks.Authenticate;

public class Client implements Runnable {
	private static final int	PORT		= 24690;
	private static Scanner		mScanner	= new Scanner(System.in);

	private User				mUser;
	private boolean				mExit		= false;

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

				break;
			case "2":

				break;
			case "3":

				break;
			case "4":

				break;
			case "5":

				break;
			case "6":

				break;
			case "7":

				break;
			case "8":
				mExit = true;
				break;

			default:
				break;
			}
		} else {
			switch (option) {
			case "1":

				break;
			case "2":

				break;
			case "3":

				break;
			case "4":

				break;
			case "5":

				break;
			case "6":
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
