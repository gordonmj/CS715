package hw6;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

public class TC implements Runnable {
	private ObjectInputStream	ois;	// object input from TA
	private ObjectOutputStream	oos;	// object output to TB

	private InputStream			is;	// input stream from TA
	private OutputStream		os;	// output stream to TB

	public TC(InputStream is, OutputStream os) {
		this.is = is;
		this.os = os;
	}

	@Override
	public void run() {
		try {
			// create object input stream from TA
			ois = new ObjectInputStream(is);

			// get object from TA
			Message n = (Message) ois.readObject();
			System.out.println(getClass().getName() + " Msg received: " + n);
			n.number = n.number + 1;

			// create object output to TB
			oos = new ObjectOutputStream(os);

			// send object to TB
			System.out.println(getClass().getName() + " Msg sent: " + n);
			oos.writeObject(n);
		} catch (Exception e) {
			System.out.println("ERROR: " + getClass().getName() + " " + e);
		} finally {
			try {
				oos.close();
				os.close();
			} catch (IOException e) {
			}
		}
	}

}