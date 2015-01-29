package project1;

public class Logger {
	private static final long	start	= System.nanoTime();

	public synchronized static void log(String comment) {
		System.out.println("[" + (System.nanoTime() - start) + "] " + comment);
	}
}
