package services.diag;

import java.sql.SQLException;

import org.json.JSONObject;

import database.DBMapper;
import services.ServicesTools;
import utils.CannotLoadConfigException;

public class DiagUtils {
	public static JSONObject checkDatabase() {
		JSONObject answer;
		
		try {
			DBMapper.getMySQLConnection();
			answer = ServicesTools.createPositiveAnswer();
		} catch (SQLException | CannotLoadConfigException e) {
			answer = new JSONObject();
			answer.put("success", false);
			answer.put("exc-class", e.getClass());
			answer.put("stack", e.getMessage());
		}
		
		return answer;
	}
	
}
