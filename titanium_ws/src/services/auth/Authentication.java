package services.auth;


import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import org.json.JSONObject;

import database.DBMapper;
import database.DBMapper.QueryType;
import database.exceptions.CannotConnectToDatabaseException;
import database.exceptions.QueryFailedException;
import services.ServicesTools;
import services.errors.ServerErrors;
import services.user.User;

public class Authentication {
	// Register queries
	private final static String QUERY_CHECK_USERNAME = "SELECT username FROM users WHERE username = LOWER(?);";
	private final static String QUERY_CHECK_EMAIL = "SELECT username FROM users WHERE email = ?;";
	private final static String QUERY_INSERT_USER = "INSERT INTO users VALUES (DEFAULT, LOWER(?), SHA2(?, 256), ?, ?);";

	// Login queries
	private final static String QUERY_GET_SALT = "SELECT salt FROM users WHERE username = LOWER(?);";
	private final static String QUERY_LOGIN = "SELECT * FROM users WHERE username = LOWER(?) AND password = SHA2(?, 256);";
	private final static String QUERY_KEY_EXISTS = "SELECT * FROM `keys` WHERE token = ?;";
	private final static String QUERY_INSERT_KEY = "INSERT INTO `keys` VALUES (?, ?, ?)";
	private final static String QUERY_DELETE_KEY = "DELETE FROM `keys` WHERE iduser = ?;";

	// Key validity queries
	private final static String QUERY_KEY = "SELECT * FROM `keys` WHERE token = ?;";
	private final static String QUERY_UPDATE_LAST_REFRESH = "UPDATE `keys` SET lastUsed = ? WHERE token = ?;";
	private final static String QUERY_IDUSER_KEY = "SELECT iduser FROM `keys` WHERE token = ?;";

	private final static String QUERY_GET_USER = "SELECT * from users WHERE idusers = ?;";
	
	private final static long KEY_VALIDITY_DURATION = 1000*60*60*24; // TODO put this in a config file. 24h ?

	public final static String LABEL_SALT = "salt";
	public final static int LABEL_IDUSER = 1;

	public final static int MIN_USERNAME_LENGTH = 6;
	public final static int MIN_PASSWORD_LENGTH = 6;
	public final static int MAX_USERNAME_LENGTH = 45;
	public final static int MAX_PASSWORD_LENGTH = 45;

	public final static int MAX_EMAIL_LENGTH = 45;
	public final static String EMAIL_REGEX = "[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}";

	/**
	 * Create an authentication key.
	 * @param username User name.
	 * @param password User password.
	 * @return Authentication key or Error message.
	 */
	public static JSONObject login(String username, String password) {
		JSONObject answer;

		String salt;

		try {
			salt = getSalt(username);

			if (salt == null) { // Can't find user in db.
				answer = ServicesTools.createJSONError(AuthErrors.WRONG_USERNAME_OR_PASSWORD);
			} else {
				ResultSet result = DBMapper.executeQuery(QUERY_LOGIN, QueryType.SELECT, username, password + salt);

				if (result.next()) { // username and password+salt combination exists.
					String key = newKey();

					// Removes old key
					removeKeyByIdUser(result.getInt(LABEL_IDUSER)); 

					// Puts new key in db.
					DBMapper.executeQuery(QUERY_INSERT_KEY, QueryType.INSERT, key, result.getInt(LABEL_IDUSER), DBMapper.getTimeNow());
					
					answer = ServicesTools.createPositiveAnswer();
					answer.put(ServicesTools.KEY_ARG, key);
				} else {
					answer = ServicesTools.createJSONError(AuthErrors.WRONG_USERNAME_OR_PASSWORD);
				}
			}
		} catch (CannotConnectToDatabaseException | QueryFailedException | SQLException e) {
			answer = ServicesTools.createDatabaseError(e);
		}

		return answer;
	}

	/**
	 * Creates a new user.
	 * @param username New user name.
	 * @param password New user password.
	 * @param email New user mail.
	 * @return Success or error message.
	 */
	public static JSONObject register(String username, String password, String email) {
		JSONObject answer;

		username = username.trim();

		// Input validity checks
		if (username.length() < MIN_USERNAME_LENGTH) {
			answer = ServicesTools.createJSONError(AuthErrors.USERNAME_TOO_SHORT);
		} else if (password.length() < MIN_PASSWORD_LENGTH) {
			answer = ServicesTools.createJSONError(AuthErrors.PASSWORD_TOO_SHORT);
		} else if (username.length() > MAX_USERNAME_LENGTH) {
			answer = ServicesTools.createJSONError(AuthErrors.USERNAME_TOO_LONG);
		} else if (password.length() > MAX_PASSWORD_LENGTH) {
			answer = ServicesTools.createJSONError(AuthErrors.PASSWORD_TOO_LONG);
		} else if (!email.matches(EMAIL_REGEX)) {
			answer = ServicesTools.createJSONError(AuthErrors.INVALID_EMAIL);
		} else {
			try {
				if (isUsernameInUse(username)){
					answer = ServicesTools.createJSONError(AuthErrors.USERNAME_IN_USE);
				} else if (isEmailInUse(email)) {
					answer = ServicesTools.createJSONError(AuthErrors.EMAIL_IN_USE);
				} else { // Valid parameters
					answer = addUserToDB(username, password, email);
				}
			} catch (CannotConnectToDatabaseException | QueryFailedException | SQLException e) {
				answer = ServicesTools.createDatabaseError(e);
			}
		}

		return answer;
	}

	/**
	 * Delete an authentication key from database.
	 * @param key Key to delete.
	 * @return Success or error message.
	 */
	public static JSONObject logout(String key) {
		JSONObject answer;
		
		try {
			if (isKeyValid(key)) {
				int idUser = getIdUserFromKey(key);
				removeKeyByIdUser(idUser);
				
				answer = ServicesTools.createPositiveAnswer();
			} else {
				answer = ServicesTools.createJSONError(ServerErrors.INVALID_KEY);
			}
		} catch (CannotConnectToDatabaseException | QueryFailedException | SQLException e) {
			answer = ServicesTools.createDatabaseError(e);
		}
		
		
		return answer;
	}
	
	/**
	 * Is this username in use by another user ?
	 */
	private static boolean isUsernameInUse(String username) throws CannotConnectToDatabaseException, QueryFailedException, SQLException {
		ResultSet result = DBMapper.executeQuery(QUERY_CHECK_USERNAME, QueryType.SELECT, username);

		return result.next();
	}

	/**
	 * Is this email in use by another user ?
	 */
	private static boolean isEmailInUse(String email) throws CannotConnectToDatabaseException, QueryFailedException, SQLException {
		ResultSet result = DBMapper.executeQuery(QUERY_CHECK_EMAIL, QueryType.SELECT, email);

		return result.next();
	}

	/**
	 * Add a new user to database.
	 * @param username User name.
	 * @param password User password.
	 * @param email User email.
	 * @return Json answer.
	 */
	private static JSONObject addUserToDB(String username, String password, String email) {
		JSONObject answer;

		String salt = newSalt();
		try {
			DBMapper.executeQuery(QUERY_INSERT_USER, QueryType.INSERT, username, password + salt, email, salt);
			answer = ServicesTools.createPositiveAnswer();
		} catch (CannotConnectToDatabaseException | QueryFailedException e) {
			answer = ServicesTools.createDatabaseError(e);
			e.printStackTrace();
		}

		return answer;
	}

	/**
	 * Create a new salt.
	 * @return salt.
	 */
	public static String newSalt() {
		String result = UUID.randomUUID().toString();
		result = result.replaceAll("-", "").substring(16);
		return result;
	}

	/**
	 * Returns user's salt.
	 * @return User's salt. This value may be null.
	 */
	private static String getSalt(String username) throws CannotConnectToDatabaseException, QueryFailedException, SQLException {
		ResultSet resultSet = DBMapper.executeQuery(QUERY_GET_SALT, QueryType.SELECT, username);

		if (!resultSet.next())
			return null;
		else
			return resultSet.getString(LABEL_SALT);
	}
	
	/**
	 * Create a new key and make sure it's not already in database.
	 * @return New key.
	 */
	private static String newKey() throws CannotConnectToDatabaseException, QueryFailedException, SQLException {
		String result;		


		ResultSet resultSet;

		do {
			result = UUID.randomUUID().toString().replaceAll("-", "");
			resultSet = DBMapper.executeQuery(QUERY_KEY_EXISTS, QueryType.SELECT, result);
		} while (resultSet.next());

		return result;
	}

	/**
	 * Removes a key from database.
	 * @param idUser Id of the key owner.
	 * @throws CannotConnectToDatabaseException
	 * @throws QueryFailedException
	 */
	private static void removeKeyByIdUser(int idUser) throws CannotConnectToDatabaseException, QueryFailedException {
		DBMapper.executeQuery(QUERY_DELETE_KEY, QueryType.DELETE, idUser);
	}

	/**
	 * Tells you if a key is valid or not.
	 * @param key Key to check.
	 * @return Is the key valide ?
	 */
	private static boolean isKeyValid(String key) throws CannotConnectToDatabaseException, QueryFailedException, SQLException {
		ResultSet resultSet = DBMapper.executeQuery(QUERY_KEY, QueryType.SELECT, key);

		if (!resultSet.next()) // No key found in DB.
			return false;

		Date lastRefresh = resultSet.getDate(3);

		if (System.currentTimeMillis() - lastRefresh.getTime() > KEY_VALIDITY_DURATION) // Key has expired.
			return false;
		else 
			return true;
	}

	/**
	 * Put the lastRefresh value to now() in database.
	 * @param key Key to refresh.
	 */
	private static boolean refreshKey(String key) throws CannotConnectToDatabaseException, QueryFailedException {
		DBMapper.executeQuery(QUERY_UPDATE_LAST_REFRESH, QueryType.UPDATE, DBMapper.getTimeNow(), key);

		return true;
	}

	/**
	 * Check if a key is valid and if it is refresh.
	 * @param key Key to check and rehfresh.
	 * @return Is the key valid.
	 */
	public static boolean validateAndRefreshKey(String key) throws CannotConnectToDatabaseException, QueryFailedException, SQLException {
		return isKeyValid(key) && refreshKey(key);
	}

	/**
	 * Returns an user id from his key.
	 * @param key User's key.
	 * @return Id of the key owner.
	 */
	public static int getIdUserFromKey(String key) throws CannotConnectToDatabaseException, QueryFailedException, SQLException {
		ResultSet result = DBMapper.executeQuery(QUERY_IDUSER_KEY, QueryType.SELECT, key);

		if (result.next())
			return result.getInt(1);
		else
			return -1;

	}

	/**
	 * Does this user id is in use ?
	 */
	public static boolean doesHeExists(int idUser) throws CannotConnectToDatabaseException, QueryFailedException, SQLException {
		ResultSet result = DBMapper.executeQuery(QUERY_GET_USER, QueryType.SELECT, idUser);

		return result.next();
	}
	
	/**
	 * Get user information from database.
	 * @param idUser User id.
	 * @return User information.
	 */
	public static User getUserFromId(int idUser) throws CannotConnectToDatabaseException, QueryFailedException, SQLException {
		ResultSet resultSet = DBMapper.executeQuery(QUERY_GET_USER, QueryType.SELECT, idUser);
		
		if (resultSet.next()) {
			String username;
			String email;
			
			username = resultSet.getString(2);
			email = resultSet.getString(4);
			
			return new User(idUser, username, email);
		} else 
			return null;
		
		
	}

}
