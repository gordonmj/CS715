package hw6;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

public class TB implements Runnable {
	private ObjectInputStream	ois;	// object input from TC
	private ObjectOutputStream	oos;	// object output to TA

	private InputStream			is1;	// input stream from TA
	private InputStream			is2;	// input stream from TC
	private OutputStream		os;	// output stream to TA

	public TB(InputStream is1, InputStream is2, OutputStream os) {
		this.is1 = is1;
		this.is2 = is2;
		this.os = os;
	}

	@Override
	public void run() {
		try {
			// receive primitive data from TA
			int data = is1.read();
			System.out.println(getClass().getName() + " received: " + data);

			// create object output stream to TA
			oos = new ObjectOutputStream(os);

			// reply to TA with message object
			Message m = new Message(10, 20);
			System.out.println(getClass().getName() + " Msg sent: " + m);
			oos.writeObject(m);

			// create object input from TC
			ois = new ObjectInputStream(is2);

			// receive object from TC
			Message n = (Message) ois.readObject();
			System.out.println(getClass().getName() + " Msg received: " + n);

		} catch (Exception e) {
			System.out.println("ERROR: " + getClass().getName() + " " + e);
		} finally {
			try {
				oos.close();
				os.close();
				is1.close();
				is2.close();
				ois.close();
			} catch (IOException e) {
			}
		}

	}

}