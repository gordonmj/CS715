package rmi.client;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.math.BigDecimal;

import rmi.compute.Compute;

public class ComputePi {
	public static void main(String args[]) {
		try {
			String name = "Compute";
			Registry registry = LocateRegistry.getRegistry("localhost", 24680);
			Compute comp = (Compute) registry.lookup(name);
			Pi task = new Pi(345);
			BigDecimal pi = comp.executeTask(task);
			System.out.println(pi);
		} catch (Exception e) {
			System.err.println("ComputePi exception:");
			e.printStackTrace();
		}
	}
}
