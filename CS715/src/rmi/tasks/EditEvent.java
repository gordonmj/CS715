package rmi.tasks;

import java.io.Serializable;

import rmi.compute.Compute;
import rmi.data.User;
import rmi.data.Event;

import java.util.Date;
import java.util.List;

public class EditEvent extends Task<Event> implements Serializable {	
	private static final long serialVersionUID = 8181235707508864582L;
	private String				mEventString = "";
	private Date				mEventDate = null;
	private Event				mEvent = null;
	
	public EditEvent(Event event, String title, Date date){
		mEventDate = date;
		mEventString = title;
		mEvent = event;
		
	}
	

	
	public Event execute() {	
		if (mEventDate != null){
			mEvent.setDate(mEventDate);
		}
		if (mEventString != ""){
			mEvent.setTitle(mEventString);
		}
		
		return mEvent;
	}
	
}
