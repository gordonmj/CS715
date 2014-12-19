package sockets.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;

public class DateTimeClient {
	private static final int	PORT	= 24690;	// default port

	public static void main(String args[]) throws Exception {
		String serverName = InetAddress.getLocalHost().getHostName();

		// try to connect to specified server and extract input stream
		Socket server = null;

		try {
			// try to get a socket connection
			server = new Socket(serverName, PORT);

			// create a input stream on the socket
			InputStream input = server.getInputStream();

			// convert the byte stream to a buffered character stream
			InputStreamReader inDateTime = new InputStreamReader(input);

			BufferedReader inBufferedDateTime = new BufferedReader(inDateTime);

			// read the Date and Time as a single line of text and print
			String result = inBufferedDateTime.readLine();

			System.out.println("Received: " + result);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		} finally {
			closeQuietly(server);
		}
	}

	public static void closeQuietly(final Socket sock) {
		if (sock != null) {
			try {
				sock.close();
			} catch (final IOException ioe) {
				// ignored
			}
		}
	}
}
