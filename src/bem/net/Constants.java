package bem.net;

public class Constants {
	public static final byte[] MAGIC = { 'K', '2', 'T', 'P' };
	final static String VERSION = "1.0.0";
	final static byte[] RESEVED = { 0x00, 0x00, 0x00, 0x00 };
	//
	public static final String OS_TYPE = "BOT";
	public static final String CP_CODE = "";
	public static final String DEVICE_ID = "";
	public static final String CLIENT_IP = "";
	public static final String PLATFORM = "";
	public static final String CLIENT_MCC = "";
	
	public static final byte[] MAGIC2 = { 'K', '2' };
	// Action
	public static final int ACTION_MAIL_SET = 35;
	public static final int ACTION_CHANGE_PASS = 33;
	public static final int ACTION_CHECK_USERNAME = 36;
	public static final int ACTION_REGISTER = 0;
	public static final int ACTION_LOGIN = 1;
	public static final int ACTION_LOGOUT = 7;
	public static final int ACTION_LIST_ROOM = 2;
	public static final int ACTION_JOIN_ROOM = 3;
	public static final int ACTION_LEAVE_ROOM = 4;
	public static final int ACTION_LIST_TABLE = 5;
	public static final int ACTION_DUPLICATE_LOGIN = 8;
	public static final int ACTION_JOIN_TABLE = 51;
	public static final int ACTION_LEAVE_TABLE = 52;
	public static final int ACTION_GET_MESSAGE = 12;
	public static final int ACTION_READ_MESSAGE = 21;
	public static final int ACTION_DELETE_MESSAGE = 13;
	public static final int ACTION_GET_EVENTS = 22;
	public static final int ACTION_GET_RANDOM_TABLE = 68;
	public static final int ACTION_NAP_THE_CAO = 38;
	// Common
	public static final int GAME_DEPOSIT = 70;
	public static final int GAME_CHAT = 50;
	public static final int GAME_START_GAME = 54;
	public static final int GAME_TOGGLE_READY = 53;
	public static final int GAME_TINH_DIEM = 62;
	public static final int GAME_SET_NUM_PLAYER = 74;
	public static final int GAME_SET_TABLE_PWD = 73;
	// Phom action
	public static final int PHOM_DRAW_CARD = 55;
	public static final int PHOM_CATCH_CARD = 56;
	public static final int DROP_CARD = 57;
	public static final int PHOM_HA_PHOM = 58;
	public static final int PHOM_REQUEST_HA_PHOM = 59;
	public static final int PHOM_U = 63;
	public static final int PHOM_GUI = 61;
	public static final int PHOM_REQUEST_GUI = 64;
	// /
	public static final int GAME_GET_LIST_INVITE_PLAYER = 66;
	public static final int GAME_INVITE_PLAYER = 67;
	public static final int GAME_INVITE_RECEIVED = 69;
	public static final int GAME_KICK_PLAYER = 60;
	// Misc
	public static final int ACTION_USER_INFO = 0x0A;
	public static final int ACTION_LIST_AVATAR = 0x12;
	public static final int ACTION_BUY_AVATAR = 0x13;
	public static final int ACTION_TOP_USER = 0x09;
	public static final int ACTION_GET_INIT_CONFIG = 0x96;
	public static final int ACTION_SEND_FEEDBACK = 0x97;
	// Friend
	public static final int ACTION_GET_LIST_FRIEND = 0x0E;
	public static final int ACTION_REQUEST_MAKE_FRIEND = 0x0F;
	public static final int ACTION_REPLY_MAKE_FRIEND_REQUEST = 0x10;
	public static final int ACTION_DELETE_FRIEND = 0x11;
	// Message
	public static final int ACTION_SEND_MSG = 0x0B;
	public static final int ACTION_HAS_MESSAGE_COMMING = 0x25;
	// /////////// BACAY ///////////////////
	public static final int BACAY_FLIP_CARD = 0xC8;
	// /////////// LIENG ///////////////////
	public static final int LIENG_BET = 76;
	public static final int LIENG_STOP = 78;
	// Quiz
	public static final int QUIZ_ANSWER = 0xD2;
	public static final int QUIZ_SET_NUM_QUESTION = 0xD3;
	// / TIENLEN MB
	public static final int TLMB_SKIP_TURN = 0x48;
	// / TIENLEN MN
	public static final int TLMN_SUPER_WIN = 0x4D; // 74
	// ///////////////////////////////////////////////////
	public static final int ERROR = 0xFA00;// 64000
	public static final int UNKNOW = 0xFF;
	public static final int DISCONNECT = 0xFE;
	public static final int PING = 0xFD;
	public static final int ERROR_SILENT = 0xFC;

	public static final int SERVICE_CHUONG_START_BLIND = 88;
	public static final int SERVICE_CHUONG_REQUEST_PRIVATE_BET = 86;
	public static final int SERVICE_CHUONG_CONFIRM_PRIVATE_BET = 89;
	// Speaker
	public static final int SPEAKER_ROOM = 0x27;
	public static final int SPEAKER_GLOBAL = 0x28;
	public static final int UPDATE_GENDER = 0x29;
	// Bar
	public static final int BUY_NEW_AVATAR = 251;
	public static final int BUY_ITEM = 0x106;
	public static final int GET_MY_ITEM = 0x107;
	public static final int GET_NUMBER_TABLE = 75;

	public static final int PRIVATE_CHAT = 0x30;
	public static final int GAMB_ROOM_CHAT = 44;

	// kqxs

	public static final int LOAD_IMAGE = 3000;
	public static final int LOAD_IMAGE_ERROR = 3001;

	// chup anh

	public static final int CARO_PLAY = 315;
	public static final int CARO_FINISH = 316;

	public static final int GET_MY_AVATAR = 275;
	public static final int CHANGE_AVATAR = 276;

	
	public static final int GET_LIST_PLAYER = 82;
	//
	public static final int GUILD_CREATE = 1000;
	public static final int GUILD_INFO = 1002;
	public static final int GUILD_TOP = 1003;
	public static final int GUILD_INVITE = 1004;
	public static final int GUILD_REQUEST_INVITE = 1005;
	public static final int GUILD_CONFIRM_INVITE = 1006;
	public static final int GUILD_CONTRIBUTION = 1007;
	public static final int GUILD_KICK_MEM = 1008;
	public static final int GUILD_QUIT = 1009;
	public static final int GUILD_LIST_MEM = 1011;
	public static final int GUILD_LIST_MEMBER_FULL = 1014;
	public static final int GUILD_REMOVED_MEMBER = 1015;

	public static final int GAME_3CAY_CHANGE_RULE = 79;
	public static final int GAME_3CAY_SHOW_ALL_CARDS = 80;
	public static final int BEM_BACAY_GA_START_NEW_GAME = 81;

	public static final int MAU_BINH_SORT_CARD_DONE = 98;
	public static final int MAU_BINH_TINH_DIEM = 99;
	public static final int MAU_BINH_WIN = 97;
	public static final int WEDDING_HONEY = 330;
	public static final int CREATE_TABLE = 350;
	public static final int QUICK_TABLE = 351;
	public static final int PLAYER_INFO = 352;
	public static final int TABLE_INFO = 353;
	public static final int SERVICE_GET_TABLE_PART_BY_PART = 354;

	public static final int TOP_PLAYER = 45;
	public static final int HELP_CONTENT = 332;
	public static final int SAVE_PROFILE = 20; 
	

	/**
 * 
 */

	/**
	 * Game ID
	 */


}
