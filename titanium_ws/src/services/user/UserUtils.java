package services.user;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import database.DBMapper;
import database.DBMapper.QueryType;
import database.exceptions.CannotConnectToDatabaseException;
import database.exceptions.QueryFailedException;
import services.ServicesTools;
import services.auth.Authentication;

public class UserUtils {
	private final static String QUERY_SEARCH_USER = "SELECT * FROM users WHERE username LIKE ? ORDER BY idusers LIMIT ? OFFSET ?;";
	
	
	public static JSONObject search(String key, String query, int page, int pageSize) {
		JSONObject answer;
		
		try {
			if (Authentication.validateAndRefreshKey(key)) {
				List<User> queryResult = getUserListFromQuery(query, page, pageSize);
				
				JSONObject result = new JSONObject();
				
				result.put("page", page);
				result.put("size", pageSize);
				
				JSONArray users = new JSONArray();
				
				for (User u : queryResult) {
					JSONObject crt = new JSONObject();
					crt.put("id", u.getId());
					crt.put("username", u.getUsername());
					users.put(crt);
				}
				result.put("users", users);
				answer = ServicesTools.createPositiveAnswer();
				answer.put("result", result);
			} else {
				answer = ServicesTools.createInvalidKeyError();
			}
		} catch (CannotConnectToDatabaseException | QueryFailedException | SQLException e) {
			answer = ServicesTools.createDatabaseError(e);
		}
		
		return answer;
	}
	
	private static List<User> getUserListFromQuery(String query, int page, int pageSize) throws CannotConnectToDatabaseException, QueryFailedException, SQLException {
		List<User> result = new ArrayList<>();
		
		ResultSet rs = DBMapper.executeQuery(QUERY_SEARCH_USER, QueryType.SELECT, '%' + query + '%', pageSize, getOffset(page, pageSize));
		
		while (rs.next()) {
			result.add(new User(rs.getInt(1), rs.getString(2), rs.getString(3)));
		}
		
		return result;
	}
	
	
	private static int getOffset(int pageNumber, int pageSize) {
		return pageNumber * pageSize;
	}
	public static void main(String[] args) throws CannotConnectToDatabaseException, QueryFailedException, SQLException {
		JSONObject json = Authentication.login("testuser", "password");
		
		String key = json.getString("key");
		
		
		System.out.println(search(key, "test", 0, 10));
		
	}
}
