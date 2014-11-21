package hw6;

import java.io.Serializable;

public class Message implements Serializable {
	private static final long	serialVersionUID	= -4677369238847472691L;
	public int					number;
	public int					id;

	public Message(int number, int id) {
		this.number = number;
		this.id = id;
	}

	@Override
	public String toString() {
		return "Message [id=" + id + ", number=" + number + "]";
	}

}