package sockets.client;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import sockets.constants.Constants;

public class Main {
	private static String	mServerName;
	static int				num_contestants	= 5;

	public static void main(String[] args) throws UnknownHostException {
		mServerName = "rival-laptop";// InetAddress.getLocalHost().getHostName();
		List<ContestantClient> contestants = new ArrayList<ContestantClient>();
		/*
		for (int i = 1; i < num_contestants + 1; i++) {
			ContestantClient c = new ContestantClient(String.valueOf(i), mServerName, Constants.PORT);
			contestants.add(c);
		}

		for (ContestantClient c : contestants) {
			new Thread(c).start();
		}
		*/
		new Thread(new ContestantClient("1", mServerName, Constants.PORT)).start();
		// new Thread(new ContestantClient("2", mServerName, Constants.PORT)).start();
		new Thread(new AnnouncerClient(mServerName, Constants.PORT)).start();

	}
}
