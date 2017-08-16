package utils;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONException;

public class Debug {
	//If DEBUG is true the program will run in debug mode.
	private  static Boolean isInDebugMode = null;
	private static String stack = "";
	
	
	/**
	 * Display a debug notice.
	 * @param notice
	 * 	String to display.
	 */
	public static void display_notice(String notice) {
		if(isInDebug())
			System.out.println("DEBUG : " + notice);
	}
	
	/**
	 * Display an exception's stack trace.
	 * @param e
	 * 	Exception to use for the stack trace's display.
	 */
	public static void display_stack(Exception e) {
		if(isInDebug()) {
			System.out.println("DEBUG ------");
			e.printStackTrace();
			System.out.println("DEBUG ------");
			
			stack += e.getMessage() + " " + e.getCause() + "\n";
			for (StackTraceElement ste : e.getStackTrace()) {
				stack += ste.toString() + "\n";
			}
		}
	}
	
	/**
	 * Display a varible's value (name + " = " + var).
	 * @param var
	 * 	Variable to display.
	 * @param name
	 * 	Display name.
	 */
	public static void display_var(Object var, String name) {
		display_notice(name + " = " + var);
	}
	
	
	public static String getStack() {
		return stack;
	}
	
	public static boolean isInDebug() {
		if (isInDebugMode == null) {
			try {
				isInDebugMode = Boolean.parseBoolean(ConfigLoader.getVar("debug"));
			} catch (JSONException | IOException e) {
				isInDebugMode = true;
				Logger.getGlobal().log(Level.WARNING, "Can't load config, debug has been set to true by default.");
				e.printStackTrace();
			}
		}
		
		
		
		return isInDebugMode;
		
	}
}


