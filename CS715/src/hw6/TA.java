package hw6;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

public class TA implements Runnable {
	private ObjectInputStream	ois;	// object input from TB
	private ObjectOutputStream	oos;	// object output stream to TC

	private InputStream			is;	// input stream from TB
	private OutputStream		os1;	// output stream to TB
	private OutputStream		os2;	// output stream to TC

	public TA(InputStream is, OutputStream os1, OutputStream os2) {
		this.is = is;
		this.os1 = os1;
		this.os2 = os2;
	}

	@Override
	public void run() {
		try {
			// send primitive data to TB
			int data = 15;
			System.out.println(getClass().getName() + " sent: " + data);
			os1.write(data);

			// create object input stream from TB
			ois = new ObjectInputStream(is);

			// get object from TB
			Message n = (Message) ois.readObject();
			System.out.println(getClass().getName() + " Msg received: " + n);
			n.number = n.number + 1;

			// create object output stream to TC
			oos = new ObjectOutputStream(os2);

			// send object to TC
			System.out.println(getClass().getName() + " Msg sent: " + n);
			oos.writeObject(n);

		} catch (Exception e) {
			System.out.println("ERROR: " + getClass().getName() + " " + e);
		} finally {
			try {
				oos.close();
				os1.close();
				os2.close();
			} catch (IOException e) {
			}
		}

	}

}