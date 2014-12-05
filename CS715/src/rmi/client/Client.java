package rmi.client;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

import rmi.compute.Compute;
import rmi.data.Event;
import rmi.data.User;
import rmi.tasks.AddEvent;
import rmi.tasks.Authenticate;
import rmi.tasks.CreateDelete;
import rmi.tasks.DeleteEvent;
import rmi.tasks.DisplayUserAccts;
import rmi.tasks.EditEvent;
import rmi.tasks.GetSchedule;
import rmi.tasks.ResetPassword;
import rmi.tasks.Task;

public class Client implements Runnable {
	private static final int	PORT		= 24690;					// default port
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
				getSchedule();
				break;
			case "5":
				// Add an event to selected user’s schedule
				addEvent();
				break;
			case "6":
				// Edit an event from selected user’s schedule
				editEvent();
				break;
			case "7":
				// Delete an event from selected user’s schedule
				deleteEvent();
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
				getSchedule();
				break;
			case "2":
				// Add an event to user’s schedule
				addEvent();
				break;
			case "3":
				// Edit an event from user’s schedule
				editEvent();
				break;
			case "4":
				// Delete an event from user’s schedule
				deleteEvent();
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
		System.out.println("Enter user name");
		String username = mScanner.nextLine();

		System.out.println("Enter password");
		String password = mScanner.nextLine();

		Object obj = executeTask(new Authenticate(username, password));
		mUser = obj == null ? null : (User) obj;

		return mUser != null;
	}

	private boolean createDeleteAcct() {
		System.out.println("Create/Delete User Account");
		boolean status = false;

		System.out.println("To delete account, leave password empty");
		System.out.println("Enter user name");
		String username = mScanner.nextLine();

		System.out.println("Enter password");
		String password = mScanner.nextLine();

		Object obj = executeTask(new CreateDelete(username, password));
		status = obj == null ? false : (boolean) obj;
		if (status == false) return status;

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
		System.out.println(reset ? "Reset Password for User Account" : "Change password to user’s account");
		boolean status = false;
		String username = mUser.getUsername();

		if (reset) {// ask for username of account to reset password
			System.out.println("Enter user name");
			username = mScanner.nextLine();
		}

		String password = null;
		if (!reset) { // ask for new password
			System.out.println("Enter new password");
			password = mScanner.nextLine();
		}

		Object obj = executeTask(new ResetPassword(username, password));
		status = obj == null ? false : (boolean) obj;
		if (status == false) return status;

		if (reset) {
			// resetting password
			System.out.println("Password reset" + (status ? " successfully" : " unsuccessfully"));
		} else {
			// changing password
			System.out.println("Password changed" + (status ? " successfully" : " unsuccessfully"));
		}
		return status;
	}

	@SuppressWarnings("unchecked")
	private boolean displayUsers() {
		System.out.println("Display List of User Account");
		Map<String, User> users = null;

		Object obj = executeTask(new DisplayUserAccts());
		users = obj == null ? null : (Map<String, User>) obj;

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

	@SuppressWarnings("unchecked")
	private boolean getSchedule() {
		System.out.println("Display schedule");
		List<Event> schedule = null;
		String username = mUser.getUsername();

		if (mUser.isAdminAcct()) {
			username = getUserInput("Enter user name");
		}

		Object obj = executeTask(new GetSchedule(username));
		schedule = obj == null ? null : (List<Event>) obj;

		if (schedule == null) {
			System.out.println("Unsuccessful finding schedule. Please check that the username is correct.");
		} else {
			printEventsToConsole(schedule);
		}
		return schedule == null;
	}

	private boolean addEvent() {
		System.out.println("Adding event");
		Event ev = null;
		String username = mUser.getUsername();

		if (mUser.isAdminAcct()) {
			username = getUserInput("Enter user name");
		}

		String title = getUserInput("Enter an event title");
		String dateString = getUserInput("Enter a date (MMM d, yyyy)");
		Date date = null;
		try {
			date = new SimpleDateFormat("MMM d, yyyy").parse(dateString);
		} catch (ParseException e1) {
			System.err.println("Date entered incorrectly.");
			return false;
		}

		Object obj = executeTask(new AddEvent(username, title, date));
		ev = obj == null ? null : (Event) obj;

		if (ev == null) {
			System.out.println("Event added unsuccessfully. Check that the username is correct.");
		} else {
			System.out.println("Event added: " + ev);
		}
		return ev == null;
	}

	private void printEventsToConsole(List<Event> events) {
		for (int i = 0; i < events.size(); i++) {
			System.out.println(i + ") " + events.get(i));
		}
	}

	@SuppressWarnings("unchecked")
	private boolean editEvent() {
		System.out.println("Edit event");
		List<Event> schedule = null;
		String username = mUser.getUsername();

		if (mUser.isAdminAcct()) {
			username = getUserInput("Enter user name");
		}

		Object obj = executeTask(new GetSchedule(username));
		schedule = obj == null ? null : (List<Event>) obj;

		if (schedule == null) {
			System.out.println("Unsuccessful finding schedule. Please check that the username is correct.");
			return false;
		}

		System.out.println("Pick from these events to edit:");
		printEventsToConsole(schedule);

		int eventId = Integer.parseInt(getUserInput("Enter the number of the event you want to edit:"));

		if (eventId < 0 || eventId >= schedule.size()) {
			System.out.println("Event number not found.");
			return false;
		}

		String title = getUserInput("Enter a new event title (or leave blank for no change)");
		String dateString = getUserInput("Enter a date (MMMM d, yyyy) (or leave blank for no change)");
		Date date = null;

		if (!dateString.equals("")) {
			try {
				date = new SimpleDateFormat("MMM d, yyyy").parse(dateString);
			} catch (ParseException e1) {
				System.err.println("Date entered incorrectly.");
				return false;
			}
		}

		Event ev = null;
		obj = executeTask(new EditEvent(username, eventId, title, date));
		ev = obj == null ? null : (Event) obj;

		if (ev == null) {
			System.out.println("Unsuccessful editing event.");
		} else {
			System.out.println("Event edited: " + ev);
		}
		return ev == null;
	}

	@SuppressWarnings("unchecked")
	private boolean deleteEvent() {
		System.out.println("Delete event");
		List<Event> schedule = null;
		String username = mUser.getUsername();

		if (mUser.isAdminAcct()) {
			username = getUserInput("Enter user name");
		}

		Object obj = executeTask(new GetSchedule(username));
		schedule = obj == null ? null : (List<Event>) obj;

		if (schedule == null) {
			System.out.println("Unsuccessful finding schedule. Please check that the username is correct.");
			return false;
		}

		System.out.println("Pick from these events to edit:");
		printEventsToConsole(schedule);

		int eventId = Integer.parseInt(getUserInput("Enter the number of the event you want to edit:"));

		if (eventId < 0 || eventId >= schedule.size()) {
			System.out.println("Event number not found.");
			return false;
		}

		obj = executeTask(new DeleteEvent(username, eventId));

		if (obj == null) System.out.println("Unsuccessful deleting event.");
		else System.out.println("Event deleted");

		return obj == null;
	}

	/**
	 * @param task
	 *            The {@link Task} to be performed
	 * @return the {@link Object} desired from the Task upon completion, null if an exception has occured.
	 */
	private Object executeTask(Task<?> task) {
		Object rObj = null;
		try {
			String name = "Event";
			Registry registry = LocateRegistry.getRegistry("localhost", PORT);
			Compute comp = (Compute) registry.lookup(name);
			rObj = comp.executeTask(task);
		} catch (Exception e) {
			rObj = null;
		}
		return rObj;
	}

	private String getUserInput(String comment) {
		System.out.println(comment);
		return mScanner.nextLine();
	}

	public static void main(String[] args) {
		new Thread(new Client()).start();
	}
}
