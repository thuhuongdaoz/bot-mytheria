package bem.event;

public class KEvent {

	public static final int CONNECTION = 0;
	public static final int CONNECTION_LOST = 1;
	public static final int LOGIN_ERROR = 2;
	public static final int LOGIN_OK = 3;
	public static final int LOGOUT_OK = 301;
	public static final int SILENT_ERROR_RECEIVED = 302;
	public static final int LOGIN_DUPLICATE = 91;
	public static final int NAP_THE_CAO_RESULT_RECEIVED = 92;
	// ////////// TABLE
	public static final int ROOM_JOIN = 4;
	public static final int ROOM_LEAVE = 401;
	public static final int CHECK_USERNAME_RECEIVED = 402;
//	public static final int TABLE_JOIN = 5;


	private String message;

	private Object[] args;

	private Object object;

	public String getMessage() {
		return message;
	}

	public void setArg(Object[] args) {
		this.args = args;
	}

	public Object[] getArgs() {
		return args;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	private int type;

	public int getType() {
		return type;
	}

	public KEvent(int type) {
		this.type = type;
	}

	public KEvent(int type, Object object) {
		this.type = type;
		this.object = object;
	}

	public Object getObject() {
		return object;
	}

	/**
	 * fire event voi argument, vd: user join table + [name,isHost]
	 * 
	 * @param type
	 *            kieu event
	 * @param args
	 *            tham so di kem cua event
	 */
	public KEvent(int type, Object[] args) {
		this.type = type;
		this.args = args;
	}

	/**
	 * fire event voi message
	 * 
	 * @param type
	 *            kieu event
	 * @param message
	 *            thong diep
	 */
	public KEvent(int type, String message) {
		this.type = type;
		setMessage(message);
	}

}
