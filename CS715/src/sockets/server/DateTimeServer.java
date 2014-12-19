package sockets.server;

import java.net.*;
import java.io.*;
import java.util.*;

class DateTimeServer {
	private static final int	PORT	= 24690;	// default port

	public static void main(String[] args) {
		ServerSocket ssock = null;

		try {
			// create a serverSocket
			ssock = new ServerSocket(PORT);

			sop("Date Time Server Running on " + InetAddress.getLocalHost().getHostName() + " on port #" + PORT);

			// wait forever for a client to connect
			while (true) {

				// accept client connection
				Socket s = ssock.accept();
				// if no clients comes in, Server is just sitting there waiting for clients

				// extract an output stream
				sop("Client Conected");

				OutputStream os = s.getOutputStream();
				sop("output stream created");
				// convert to a character output stream

				PrintWriter out = new PrintWriter(os);
				sop("input stream created");
				// pick up the Date and time

				Date date = new Date();

				// send the Date/Time to the outputstream
				out.println(date);
				sop("Date Sent to Client");

				// close the output stream

				out.close();
				sop("Output stream closed");

				// close the connection to the client
				s.close();
				sop("Conection to the client closed");

			} // end while

		} catch (IOException e) {
			return;
		} finally {
			closeQuietly(ssock);
		}

	} // end main

	public static void sop(String t) {
		System.out.println(t);
	}

	public static void closeQuietly(final ServerSocket sock) {
		if (sock != null) {
			try {
				sock.close();
			} catch (final IOException ioe) {
				// ignored
			}
		}
	}

} // end DateTimeServer

