package data;

public class Event {
	public String id;
	public String message;
	public String dateBegin;
	public String dateEnd;
	
	public Event(String id, String message,String dateBegin,String dateEnd) {
		this.id = id;
		this.message = message;		
		this.dateBegin = dateBegin.substring(6, dateBegin.length());
		this.dateEnd = dateEnd.substring(6, dateEnd.length());
	}
}
