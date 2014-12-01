package rmi.main;

import rmi.client.Client;

public class Main {

	public static void main(String args[]) {
		new Thread(new Client()).start();
	}
}
