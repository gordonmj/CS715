package rmi.main;

import rmi.client.Client;
import rmi.engine.EventEngine;

public class Main {
	private static final int	PORT	= 24690;

	public static void main(String args[]) {
		new Thread(new EventEngine(PORT)).start();
		new Thread(new Client(PORT)).start();
	}
}
