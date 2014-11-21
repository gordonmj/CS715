package project;

import java.util.ArrayList;
import java.util.List;

public class Main {
	private static int	num_contestants	= 9;

	public static void main(String[] args) {
		List<Contestant> contestants = new ArrayList<Contestant>();

		for (int i = 0; i < num_contestants; i++) {
			Contestant c = new Contestant(String.valueOf(i + 1), num_contestants);
			contestants.add(c);
		}

		new Thread(new Announcer(contestants)).start();

		for (Contestant c : contestants) {
			new Thread(c).start();
		}

		// new Thread(new Host()).start();
	}

}
