package services.organization;

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
import services.user.User;

public class OrganizationUtils {
	// Create organization
	private final static String QUERY_INSERT_ORGA = "INSERT INTO organizations VALUES (DEFAULT, ?, ?)";
	private final static String QUERY_NAME_IN_USE = "SELECT * FROM organizations WHERE idorga = ?;";

	// Remove organization
	private final static String QUERY_REMOVE_ORGA = "DELETE FROM organizations WHERE idorga = ?;";
	private final static String QUERY_REMOVE_MEMBERS = "DELETE FROM members WHERE idorga = ?;";

	// List organization
	private final static String QUERY_GET_OWNED = "SELECT * FROM organizations WHERE owner = ?;";
	private final static String QUERY_GET_ORG = "SELECT * FROM organizations WHERE idorga = ?;";
	private final static String QUERY_GET_ORG_BY_MEMBER = "SELECT idorga FROM members WHERE iduser = ?;";

	// Add member
	private final static String QUERY_ADD_MEMBER = "INSERT INTO members VALUES (?, ?)";

	// Remove member
	private final static String QUERY_REMOVE_MEMBER = "DELETE FROM members WHERE idorga = ? AND iduser = ?";

	// List members
	private final static String QUERY_GET_MEMBERS = "SELECT iduser FROM members WHERE idorga = ?;";

	// Transfer ownership
	private final static String QUERY_CHANGE_OWNER = "UPDATE organizations SET owner = ? WHERE idorga = ?;";

	// Permissions
	private final static String QUERY_IS_OWNER = "SELECT * FROM organizations WHERE idorga = ? AND owner = ?;";
	private final static String QUERY_IS_MEMBER = "SELECT * FROM members WHERE idorga = ? AND iduser = ?;";

	/**
	 * Creates an organization.
	 * @param key Authentication key.
	 * @param name New organization name.
	 */
	public static JSONObject createOrganization(String key, String name) {
		JSONObject answer;

		try {
			if (Authentication.validateAndRefreshKey(key)) { // Key is valid.
				if (isNameInUse(name)) { // Name is in use.
					answer = ServicesTools.createJSONError(OrgaErrors.NAME_IN_USE);
				} else { // Name is available.
					int idUser = Authentication.getIdUserFromKey(key);
					DBMapper.executeQuery(QUERY_INSERT_ORGA, QueryType.INSERT, idUser, name);

					answer = ServicesTools.createPositiveAnswer();
				}
			} else {
				answer = ServicesTools.createInvalidKeyError();
			}
		} catch (CannotConnectToDatabaseException | QueryFailedException | SQLException e) {
			answer = ServicesTools.createDatabaseError(e);
		}

		return answer;
	}

	/**
	 * Removes an organization from database. It also removes member association with this organization.
	 * @param key Owner's key.
	 * @param idOrga Organization to remove.
	 */
	public static JSONObject removeOrganization(String key, int idOrga) {
		JSONObject answer;

		try {
			if (Authentication.validateAndRefreshKey(key)) { // The key is valid.
				int idUser = Authentication.getIdUserFromKey(key);

				if (hasOwnership(idUser, idOrga)) {
					// Removes members.
					DBMapper.executeQuery(QUERY_REMOVE_MEMBERS, QueryType.DELETE, idOrga);
					// Removes organization.
					DBMapper.executeQuery(QUERY_REMOVE_ORGA, QueryType.DELETE, idOrga); 

					answer = ServicesTools.createPositiveAnswer();
				} else {
					answer = ServicesTools.createJSONError(OrgaErrors.OWNERSHIP_REQ);
				}
			} else {
				answer = ServicesTools.createInvalidKeyError();
			}
		} catch (CannotConnectToDatabaseException | QueryFailedException | SQLException e) {
			answer = ServicesTools.createDatabaseError(e);
		}

		return answer;
	}

	/**
	 * List every organization related to an user.
	 * @param key Authentication key.
	 */
	public static JSONObject listOrganizations(String key) {
		JSONObject answer;

		try {
			if (Authentication.validateAndRefreshKey(key)) { // The key is valid.
				int idUser = Authentication.getIdUserFromKey(key);
				List<Organization> orgs = new ArrayList<>();

				// Owned orgs.
				orgs.addAll(getOwnedOrganizations(idUser));
				// Organization his member of.
				orgs.addAll(getOrganizationByMember(idUser));

				answer = ServicesTools.createPositiveAnswer();

				// Orgs to json.
				JSONArray orgas = new JSONArray();
				for (Organization org : orgs) {
					JSONObject data = new JSONObject();
					data.put("id", org.getId());
					data.put("name", org.getName());
					data.put("owner", org.getOwner().getId());
					orgas.put(data);
				}
				answer.put("organizations", orgas);

			} else {
				answer = ServicesTools.createInvalidKeyError();
			}
		} catch (CannotConnectToDatabaseException | QueryFailedException | SQLException e) {
			answer = ServicesTools.createDatabaseError(e);
		}

		return answer;
	}

	/**
	 * Add a member to an organization. Only the owner of the organization can do that.
	 * @param key Authentication key.
	 * @param idOrga Organization's id.
	 * @param idUser User's id.
	 */
	public static JSONObject addMember(String key, int idOrga, int idUser) {
		JSONObject answer;

		try {
			if (Authentication.validateAndRefreshKey(key)) { // Key is Valid.
				int idOwner = Authentication.getIdUserFromKey(key);
				if (Authentication.doesHeExists(idUser)) { // The user exists.
					if (!isMember(idUser, idOrga)) { 
						if (hasOwnership(idOwner, idOrga)) { // The user own the organization.
							DBMapper.executeQuery(QUERY_ADD_MEMBER, QueryType.INSERT, idOrga, idUser);
							answer = ServicesTools.createPositiveAnswer();
						} else {
							answer = ServicesTools.createJSONError(OrgaErrors.OWNERSHIP_REQ);
						}

					} else {
						answer = ServicesTools.createJSONError(OrgaErrors.ALREADY_MEMBER);
					}
				} else {
					answer = ServicesTools.createJSONError(OrgaErrors.UKN_USER);
				}
			} else {
				answer = ServicesTools.createInvalidKeyError();
			}
		} catch (CannotConnectToDatabaseException | QueryFailedException | SQLException e) {
			answer = ServicesTools.createDatabaseError(e);
		}

		return answer;
	}

	/**
	 * Removes an user from the organization.
	 * @param key Authentication key.
	 * @param idOrga Organization's id.
	 * @param idUser User's id.
	 */
	public static JSONObject removeMember(String key, int idOrga, int idUser) {
		JSONObject answer;

		try {
			if (Authentication.validateAndRefreshKey(key)) { // Key is valid.
				int idOwner = Authentication.getIdUserFromKey(key);
				if (Authentication.doesHeExists(idUser)) { // User exists.
					if (isMember(idUser, idOrga)) { 
						if (hasOwnership(idOwner, idOrga)) {
							DBMapper.executeQuery(QUERY_REMOVE_MEMBER, QueryType.INSERT, idOrga, idUser);
							answer = ServicesTools.createPositiveAnswer();
						} else {
							answer = ServicesTools.createJSONError(OrgaErrors.OWNERSHIP_REQ);
						}

					} else {
						answer = ServicesTools.createJSONError(OrgaErrors.MEMBERSHIP_REQ);
					}
				} else {
					answer = ServicesTools.createJSONError(OrgaErrors.UKN_USER);
				}
			} else {
				answer = ServicesTools.createInvalidKeyError();
			}
		} catch (CannotConnectToDatabaseException | QueryFailedException | SQLException e) {
			answer = ServicesTools.createDatabaseError(e);
		}

		return answer;
	}
	
	/**
	 * List member of an organization.
	 * @param key Authentication key.
	 * @param idOrga Organization's id.
	 */
	public static JSONObject listMembers(String key, int idOrga) {
		JSONObject answer;

		try {
			if (Authentication.validateAndRefreshKey(key)) { // Key is valid.
				int idOwner = Authentication.getIdUserFromKey(key);
				if (hasOwnership(idOwner, idOrga)) { // Has the ownership.
					List<User> users = getMembers(idOrga);

					answer = ServicesTools.createPositiveAnswer();
					
					JSONArray userArray = new JSONArray();
					JSONObject crt = new JSONObject();
					crt.put("id", idOwner);
					crt.put("username", Authentication.getUserFromId(idOwner).getUsername());
					userArray.put(crt);
					// User to json
					for (User user : users) {
						crt = new JSONObject();
						crt.put("id", user.getId());
						crt.put("username", user.getUsername());
						userArray.put(crt);
					}
					
					answer.put("members", userArray);

				} else {
					answer = ServicesTools.createJSONError(OrgaErrors.OWNERSHIP_REQ);
				}


			} else {
				answer = ServicesTools.createInvalidKeyError();
			}
		} catch (CannotConnectToDatabaseException | QueryFailedException | SQLException e) {
			answer = ServicesTools.createDatabaseError(e);
		}

		return answer;
	}

	/**
	 * Gives organization ownership to someone else.
	 * @param key Authentication key.
	 * @param idOrga Organization id.
	 * @param idUser Id of the new owner.
	 */
	public static JSONObject transferOwnership(String key, int idOrga, int idUser) {
		JSONObject answer;

		try {
			if (Authentication.validateAndRefreshKey(key)) { // Key is valid.
				int idOwner = Authentication.getIdUserFromKey(key);
				if (Authentication.doesHeExists(idUser)) { // New owner exists.
					if (hasOwnership(idOwner, idOrga)) { // The user can perform this operation.
						DBMapper.executeQuery(QUERY_CHANGE_OWNER, QueryType.INSERT, idUser, idOrga);
						answer = ServicesTools.createPositiveAnswer();
					} else {
						answer = ServicesTools.createJSONError(OrgaErrors.OWNERSHIP_REQ);
					}

				} else {
					answer = ServicesTools.createJSONError(OrgaErrors.UKN_USER);
				}

			} else {
				answer = ServicesTools.createInvalidKeyError();
			}
		} catch (CannotConnectToDatabaseException | QueryFailedException | SQLException e) {
			answer = ServicesTools.createDatabaseError(e);
		}

		return answer;
	}

	/**
	 * Returns every member of an organization.
	 * @param idOrga Organization's id.
	 * @return List of members.
	 */
	private static List<User> getMembers(int idOrga) throws CannotConnectToDatabaseException, QueryFailedException, SQLException {
		ArrayList<User> result = new ArrayList<>();

		ResultSet resultSet = DBMapper.executeQuery(QUERY_GET_MEMBERS, QueryType.SELECT, idOrga);

		while(resultSet.next()) {
			result.add(Authentication.getUserFromId(resultSet.getInt(1)));
		}

		return result;
	}

	/**
	 * List every organization owned by an user.
	 * @param idUser User's id.
	 * @return List of organization.
	 */
	private static List<Organization> getOwnedOrganizations(int idUser) throws CannotConnectToDatabaseException, QueryFailedException, SQLException {
		ArrayList<Organization> result = new ArrayList<>();

		ResultSet resultSet = DBMapper.executeQuery(QUERY_GET_OWNED, QueryType.SELECT, idUser);


		while(resultSet.next()) {
			result.add(new Organization(resultSet.getInt(1), resultSet.getString(3),
					Authentication.getUserFromId(resultSet.getInt(2))));
		}

		return result;
	}

	/**
	 * Get every organization an user is member of.
	 * @param idUser Users id.
	 * @return List of organizations.
	 */
	private static List<Organization> getOrganizationByMember(int idUser) throws CannotConnectToDatabaseException, QueryFailedException, SQLException {
		ArrayList<Organization> result = new ArrayList<>();
		ResultSet resultSet = DBMapper.executeQuery(QUERY_GET_ORG_BY_MEMBER, QueryType.SELECT, idUser);

		while (resultSet.next()) {
			result.add(getOrganizationById(resultSet.getInt(1)));
		}

		return result;
	}

	/**
	 * Get an organization.
	 * @param idOrga Orgnaization's id.
	 */
	private static Organization getOrganizationById(int idOrga) throws SQLException, CannotConnectToDatabaseException, QueryFailedException {
		ResultSet result = DBMapper.executeQuery(QUERY_GET_ORG, QueryType.SELECT, idOrga);

		if(result.next()) {
			return new Organization(result.getInt(1), result.getString(3),
					Authentication.getUserFromId(result.getInt(2)));
		} else {
			return null;
		}
	}

	/**
	 * Is this name is use by an organization ?
	 */
	private static boolean isNameInUse(String name) throws SQLException, CannotConnectToDatabaseException, QueryFailedException {
		ResultSet result = DBMapper.executeQuery(QUERY_NAME_IN_USE, QueryType.SELECT, name);

		return result.next();
	}

	/**
	 * Does this user owns this organization ?
	 */
	public static boolean hasOwnership(int idUser, int idOrga) throws CannotConnectToDatabaseException, QueryFailedException, SQLException {
		ResultSet result = DBMapper.executeQuery(QUERY_IS_OWNER, QueryType.SELECT, idOrga, idUser);

		return result.next();
	}
	
	/**
	 * Does this user is a member of this organization ?
	 */
	public static boolean isMember(int idUser, int idOrga) throws CannotConnectToDatabaseException, QueryFailedException, SQLException {
		ResultSet result = DBMapper.executeQuery(QUERY_IS_MEMBER, QueryType.SELECT, idOrga, idUser);

		return result.next();
	}

}
