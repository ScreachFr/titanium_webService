package utils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

public class ConfigLoader {
	public final static String DB_HOST = "db-host";
	public final static String DB_PORT = "db-port";
	public final static String DB_DATABASE = "db-database";
	public final static String DB_LOGIN = "db-login";
	public final static String DB_PASSWORD = "db-password";
	public final static String DB_USE_SSL = "db-use-ssl";
	
	private final static String CONFIG_PATH = "/opt/titanium_ws/config.json";
	
	
	private static Map<String, String> vars = null;
	
	
	private static void loadConfigFile() throws JSONException, IOException {
		vars = new HashMap<>();
		
		JSONObject root = new JSONObject(FileLoader.loadFile(CONFIG_PATH));
		
		for (String key : root.keySet()) {
			vars.put(key, root.getString(key));
		}
		
	}
	
	public static String getVar(String key) throws JSONException, IOException {
		if (vars == null)
			loadConfigFile();
		
		return vars.get(key);
	}
	
	public static void main(String[] args) {
		try {
			System.out.println(getVar(DB_HOST));
			System.out.println(getVar(DB_PORT));
		} catch (JSONException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
