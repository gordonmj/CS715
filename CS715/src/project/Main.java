package project;

import java.util.ArrayList;
import java.util.List;

public class Main {
	// Contestant variables: num_contestants, questionValues
	static int		num_contestants	= 13;
	static int		questionValues	= 200;

	// Game variables: numRounds, numQuestions
	static int		numRounds		= 2;
	static int		numQuestions	= 5;

	// Room variables: room_capacity
	static int		room_capacity	= 4;

	// Answer variables: rightPercent
	static double	rightPercent	= 0.65;

	public static void main(String[] args) {

		if (args.length == 6) {
			// Contestant variables: num_contestants, questionValues
			num_contestants = Integer.parseInt(args[5]);
			questionValues = Integer.parseInt(args[2]);

			// Game variables: numRounds, numQuestions
			numRounds = Integer.parseInt(args[0]);
			numQuestions = Integer.parseInt(args[1]);

			// Room variables: room_capacity
			room_capacity = Integer.parseInt(args[4]);

			// Answer variables: rightPercent
			rightPercent = Double.parseDouble(args[3]);
		}

		List<Contestant> contestants = new ArrayList<Contestant>();

		for (int i = 0; i < num_contestants; i++) {
			Contestant c = new Contestant(String.valueOf(i + 1), num_contestants, questionValues, numRounds, numQuestions, room_capacity);
			contestants.add(c);
		}

		new Thread(new Announcer(contestants, rightPercent)).start();

		for (Contestant c : contestants) {
			new Thread(c).start();
		}

	}
}
