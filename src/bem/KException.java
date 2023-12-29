package bem;

public class KException extends Exception{
	
	public static final int ACCOUNT_LOCKED = 0;
	public static final int ILLEGAL_IDENTIFY = 1;
	public static final int LOGIN_REFUSED = 2;
	public static final int NO_CONFERENCE = 3;
	public static final int BAD_USERNAME = 4;
	public static final int BAD_FORMAT = 5;
	public static final int BAD_PASSWORK = 6;
	public static final int UNKNOW = 7;
	
	private int type;
	
	public KException(String message) {
		super(message);
	}
	
	public KException(int type,String message) {
		this(message);
		this.type = type;
	}
	
	public int getType(){
		return type;
	}
	
}
