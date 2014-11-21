package hw6;

import java.io.PipedInputStream;
import java.io.PipedOutputStream;

public class hw6 {

	public static void main(String[] args) {
		try {
			System.out.println("Pipe setup");
			// TA output pipes
			PipedOutputStream ta_tb_pos = new PipedOutputStream();
			PipedOutputStream ta_tc_pos = new PipedOutputStream();

			// TB output pipes
			PipedOutputStream tb_ta_pos = new PipedOutputStream();

			// TC output pipes
			PipedOutputStream tc_tb_pos = new PipedOutputStream();

			// TA input pipes
			PipedInputStream ta_tb_pis = new PipedInputStream(tb_ta_pos);

			// TB input pipes
			PipedInputStream tb_ta_pis = new PipedInputStream(ta_tb_pos);
			PipedInputStream tb_tc_pis = new PipedInputStream(tc_tb_pos);

			// TC input pipes
			PipedInputStream tc_ta_pis = new PipedInputStream(ta_tc_pos);

			// thread creation
			TA ta = new TA(ta_tb_pis, ta_tb_pos, ta_tc_pos);
			TB tb = new TB(tb_ta_pis, tb_tc_pis, tb_ta_pos);
			TC tc = new TC(tc_ta_pis, tc_tb_pos);

			System.out.println("Thread execution");
			new Thread(ta).start();
			new Thread(tb).start();
			new Thread(tc).start();

		} catch (Exception e) {

		} finally {

		}
	}
}
