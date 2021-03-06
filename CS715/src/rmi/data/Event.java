package rmi.data;

import java.io.Serializable;
import java.util.Date;

public class Event implements Comparable<Event>, Serializable {
	private static final long	serialVersionUID	= 1454757449983887756L;

	private String				mTitle				= "";
	private Date				mDate				= new Date();
	private int					mEventId			= 0;

	public Event(String title, Date date, int eventId) {
		mTitle = title;
		mDate = date;
		mEventId = eventId;
	}

	public String getTitle() {
		return mTitle;
	}

	public void setTitle(String title) {
		mTitle = title;
	}

	public Date getDate() {
		return mDate;
	}

	public void setDate(Date date) {
		mDate = date;
	}

	public int getEventId() {
		return mEventId;
	}

	@Override
	public String toString() {
		return "ID: " + mEventId + "\tTitle: " + mTitle + "\tDate: " + mDate;
	}

	@Override
	public int compareTo(Event event) {
		return mDate.compareTo(event.getDate());
	}

}
