package bem;

public class Logger {
	public static void log(String type,String message){
		System.out.println(type+": "+message);
	}
	
	public static void info(String message){
		if (Configuration.DEBUG_CONSOLE)
			log("info",message);
	}
	
	public static void debug(String message){
		if (Configuration.DEBUG_CONSOLE)
			log("debug",message);
	}
	
	public static void error(String message){
		if (Configuration.DEBUG_CONSOLE)
			log("error",message);
	}

}
