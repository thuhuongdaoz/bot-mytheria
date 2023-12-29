package bem;

import java.util.Vector;

public class Configuration {

	public static final boolean DEBUG_SMS = false;
	public static final boolean DEBUG_CONSOLE = true;
	public static final boolean DEBUG_FPS = false;
	
//	svrInfo = new ServerInfo ("g0.gamemonaco.com", 6666, 999, "svr test", 0);

//	//test
//	public static final String HOST_PROTO =  "g0.gamemonaco.com";
//	public static final int PORT_PROTO = 6666;

	
	// proto
//	public static final String HOST_PROTO = "27.0.14.23";
//	public static final int PORT_PROTO = 8081;
//	localhost
	public static final String HOST_PROTO = "127.0.0.1";
//	sv dev
//	public static final String HOST_PROTO = "103.9.206.228";
//	//sv test
//	public static final String HOST_PROTO = "103.9.206.247";

	public static final int PORT_PROTO = 8889;
	//
	public static final int INTERVAL_ADD_PKG = 500;

	// ////////
	// alternative
	public static final String ALT_HOST = "103.9.206.228";
	// port

	public static final String OS_TYPE = "ANDROID";

	// DEF = K2TEK, YOTEL, VNO, VPR, V9X
	// ///////////////////////////////////////////////////////////////////
	// Thay define o day khi clone CP
	// #define VNO
	// ///////////////////////////////////////////////////////////////////

	// Config for CP - k2tek
	public static String CP = "cp1";
	
	}
