package project1;

public class Logger {

	public synchronized static void log(String comment) {
		System.out.println(System.nanoTime() + " " + comment);
	}
}
