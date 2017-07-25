package services;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import database.DataBaseErrors;
import database.exceptions.CannotConnectToDatabaseException;
import database.exceptions.QueryFailedException;
import services.errors.ServerErrors;
import services.errors.ServletError;
import utils.Debug;

public class ServicesTools {
	//Common args name.
	public final static String KEY_ARG			= "key";
	public final static String IDORGA_ARG 		= "idorga";
	public final static String IDUSER_ARG 		= "iduser";
	public final static String USERNAME_ARG 	= "username";
	public final static String PASSWORD_ARG 	= "password";
	public final static String EMAIL_ARG 		= "email";
	public final static String NAME_ARG 		= "name";
	public final static String HOST_ARG 		= "host";
	public final static String PORT_ARG 		= "port";
	public final static String IDSERVER_ARG		= "idserver";
	public final static String COMMAND_ARG 		= "command";
	public final static String TIMEOUT_ARG 		= "timeout";
	
	
	/**
	 * Check if there's a null in params.
	 * @param objs
	 * 	Objects to test.
	 * @return
	 * 	True = there is a null. False = no nulls.
	 */
	public static boolean nullChecker(Object... objs) {
		for (Object object : objs) {
			if (object == null)
				return true;
		}

		return false;
	}


	public static JSONObject createJSONError(ServletError error) {
		JSONObject json = new JSONObject();
		json.put("errorCode", error.getCode());
		json.put("errorMessage", error.getMessage());

		return json;
	}

	public static JSONObject createPositiveAnswer() {
		JSONObject ret = new JSONObject();

		ret.put("success", true);

		return ret;
	}


	public static String hex(byte[] array) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < array.length; ++i) {
			sb.append(Integer.toHexString((array[i]
					& 0xFF) | 0x100).substring(1,3));        
		}
		return sb.toString();
	}

	public static String md5Hex(String message) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			return hex (md.digest(message.getBytes("CP1252")));
		} catch (NoSuchAlgorithmException e) {
		} catch (UnsupportedEncodingException e) {
		}
		return null;
	}


	public static void addCORSHeader(HttpServletResponse resp) {
		resp.addHeader("Access-Control-Allow-Origin", "*");
	}
	
	public static JSONObject createDatabaseError(Exception e) {
		JSONObject result;
		
		if (e instanceof SQLException) {
			Debug.display_stack(e);
			result = createJSONError(DataBaseErrors.UKNOWN_SQL_ERROR);
		} else if (e instanceof CannotConnectToDatabaseException) {
			result = createJSONError(DataBaseErrors.CANNOT_CONNECT_TO_DATABASE);
		} else if (e instanceof QueryFailedException) {
			result = createJSONError(DataBaseErrors.QUERY_FAILED);
		} else {
			Debug.display_stack(e);
			result = createJSONError(DataBaseErrors.UKNOWN_DB_EXCEPTION);
		}
		
		return result;
	}
	
	public static JSONObject createInvalidKeyError() {
		return createJSONError(ServerErrors.INVALID_KEY);
	}
}















