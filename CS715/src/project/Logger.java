package project;

import java.util.PriorityQueue;

public class Logger {

	private static PriorityQueue<String>	pq	= new PriorityQueue<>();

	public static void log(String comment) {
		synchronized (pq) {
			pq.add(System.nanoTime() + " " + comment);
		}
	}

	public static void print() {
		synchronized (pq) {
			while (!pq.isEmpty()) {
				System.out.println(pq.poll());
			}
		}
	}
}
