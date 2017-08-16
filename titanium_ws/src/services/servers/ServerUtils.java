package services.servers;

import java.io.IOException;
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
import services.organization.OrgaErrors;
import services.organization.OrganizationUtils;
import services.servers.core.ConnectionFailureException;
import services.servers.core.Server;
import utils.Debug;

public class ServerUtils {
	// Duplication
	private final static String QUERY_CHECK_NAME = "SELECT * FROM servers WHERE name = ? AND owner = ?;";
	private final static String QUERY_CHECK_HOST_PORT = "SELECT * FROM servers WHERE host = ? AND port = ? AND owner = ?;";

	// Add server
	private final static String QUERY_ADD_SERVER = "INSERT INTO servers VALUES (DEFAULT, ?, ?, ?, ?, ?);";

	// Remove server
	private final static String QUERY_REMOVE_SERVER = "DELETE FROM servers WHERE idserver = ?;";

	// Edit server
	private final static String QUERY_EDIT_SERVER = "UPDATE servers SET host = ?, port = ?, password = ?, name = ? WHERE idserver = ?;";

	// List servers
	private final static String QUERY_LIST_SERVERS = "SELECT * FROM servers WHERE owner = ?;";
	
	private final static String QUERY_GET_SERVER = "SELECT * FROM servers WHERE idserver = ?;";

	/**
	 * Add a server to an organization.
	 * @param key Authentication key.
	 * @param idOrga Organization id.
	 * @param name New server name.
	 * @param host New server host.
	 * @param port New server port.
	 * @param password New server password.
	 */
	public static JSONObject addServer(String key, int idOrga, String name, String host, int port, String password) {
		JSONObject answer;

		try {
			if (Authentication.validateAndRefreshKey(key)) {
				if (isNameInUse(name, idOrga)) {
					answer = ServicesTools.createJSONError(ServerErrors.DUPLICATED_SERVER_NAME);
				} else { // Name is available.
					if (isHostAndPortInUse(host, port, idOrga)) {
						answer = ServicesTools.createJSONError(ServerErrors.DUPLICATED_SERVER);
					} else { // Host and port are available.
						DBMapper.executeQuery(QUERY_ADD_SERVER, QueryType.INSERT, idOrga, host, port, password, name);
						answer = ServicesTools.createPositiveAnswer();
					}
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
	 * removes a server from an organization. It completely removes the server from database.
	 * @param key Authentication key.
	 * @param idServer Server to remove.
	 */
	public static JSONObject removeServer(String key, int idServer) {
		JSONObject answer;

		try {
			if (Authentication.validateAndRefreshKey(key)) {
				int idUser = Authentication.getIdUserFromKey(key);
				if (serverExists(idServer)) {
					if (canModify(idUser, idServer)) {
						DBMapper.executeQuery(QUERY_REMOVE_SERVER, QueryType.DELETE, idServer);
						answer = ServicesTools.createPositiveAnswer();
					} else {
						answer = ServicesTools.createJSONError(OrgaErrors.OWNERSHIP_REQ);
					}
				} else {
					answer = ServicesTools.createJSONError(ServerErrors.CANNOT_FIND_SERVER);
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
	 * Changes server informations.
	 * @param key Authentication key.
	 * @param idServer Server's id.
	 * @param name Server's new name.
	 * @param host Server's new host.
	 * @param port Server's new port.
	 * @param password Server's new password.
	 */
	public static JSONObject editServer(String key, int idServer, String name, String host, int port, String password) {
		JSONObject answer;

		try {
			if (Authentication.validateAndRefreshKey(key)) {
				int idUser = Authentication.getIdUserFromKey(key);

				if (serverExists(idServer)) {
					if (canModify(idUser, idServer)) {
						DBMapper.executeQuery(QUERY_EDIT_SERVER, QueryType.UPDATE, host, port, password, name, idServer);
						answer = ServicesTools.createPositiveAnswer();
					} else {
						answer = ServicesTools.createJSONError(OrgaErrors.OWNERSHIP_REQ);
					}
				} else {
					answer = ServicesTools.createJSONError(ServerErrors.CANNOT_FIND_SERVER);
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
	 * Checks if you can connect to a server.
	 * @param key Authentication key.
	 * @param idServer Server's id.
	 */
	public static JSONObject checkConnection(String key, int idServer) {
		JSONObject answer;

		try {
			if (Authentication.validateAndRefreshKey(key)) {
				int idUser = Authentication.getIdUserFromKey(key);

				if (serverExists(idServer)) {
					if (canAccess(idUser, idServer)) {
						Server server = getServer(idServer);

						try {
							server.connect();
							answer = ServicesTools.createPositiveAnswer();
							server.disconnect();
						} catch (IllegalStateException | IOException | ConnectionFailureException e) {
							answer = ServicesTools.createJSONError(ServerErrors.CANNOT_CONNECT, e);
							if (Debug.isInDebug())
								e.printStackTrace();
						}

					} else {
						answer = ServicesTools.createJSONError(OrgaErrors.MEMBERSHIP_REQ);
					}
				} else {
					answer = ServicesTools.createJSONError(ServerErrors.CANNOT_FIND_SERVER);
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
	 * Execute a command on the server.
	 * @param key Authentication key.
	 * @param idServer Server's id.
	 * @param command Command to execute.
	 * @param timeout Timeout.
	 */
	public static JSONObject executeCommand(String key, int idServer, String command, long timeout) {
		JSONObject answer;

		try {
			if (Authentication.validateAndRefreshKey(key)) {
				int idUser = Authentication.getIdUserFromKey(key);

				if (serverExists(idServer)) {
					if (canAccess(idUser, idServer)) {
						Server server = getServer(idServer);
						try {
							server.connect();

							server.executeCommand(command);
							answer = ServicesTools.createPositiveAnswer();
							String serverAnswer;
							try {
								serverAnswer = server.getNextAnswer(timeout);
								if (serverAnswer == null)
									serverAnswer = "none";
								answer.put("answer", serverAnswer);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							
							server.disconnect();
						} catch (IllegalStateException | IOException | ConnectionFailureException e) {
							answer = ServicesTools.createJSONError(ServerErrors.CANNOT_CONNECT, e);
						} 

					} else {
						answer = ServicesTools.createJSONError(OrgaErrors.MEMBERSHIP_REQ);
					}
				} else {
					answer = ServicesTools.createJSONError(ServerErrors.CANNOT_FIND_SERVER);
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
	 * List every available servers owned by an organization.
	 * @param key Authentication key.
	 * @param idOrga Organization's id.
	 */
	public static JSONObject listServers(String key, int idOrga) {
		JSONObject answer;
		
		try {
			if (Authentication.validateAndRefreshKey(key)) {
				int idUser = Authentication.getIdUserFromKey(key);
				if (OrganizationUtils.isMember(idUser, idOrga) || OrganizationUtils.hasOwnership(idUser, idOrga)) {
					List<Server> servers = getServerListByOrganization(idOrga);
					
					answer = ServicesTools.createPositiveAnswer();
					
					JSONArray serverList = new JSONArray();
					
					for (Server server : servers) {
						JSONObject crt = new JSONObject();
						crt.put("id", server.getId());
						crt.put("name", server.getName());
						crt.put("address", server.getAddress() + ":" + server.getPort());
						
						serverList.put(crt);
					}
					
					answer.put("servers", serverList);
				} else {
					answer = ServicesTools.createJSONError(OrgaErrors.MEMBERSHIP_REQ);
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
	 * Returns every servers owned by an organization.
	 */
	private static List<Server> getServerListByOrganization(int idOrga) throws CannotConnectToDatabaseException, QueryFailedException, SQLException {
		ArrayList<Server> result = new ArrayList<>();
		String name, address, password;
		int id, port;
		Server s;
		
		ResultSet resultSet = DBMapper.executeQuery(QUERY_LIST_SERVERS, QueryType.SELECT, idOrga);
		
		while (resultSet.next()) {
			id = resultSet.getInt(1);
			address = resultSet.getString(3);
			port = resultSet.getInt(4);
			password = resultSet.getString(5);
			name = resultSet.getString(6);
			s = new Server(id, name, address, port, password);
			result.add(s);
		}
		
		return result;
	}
	
	/**
	 * Is this name in use by another server in this organization ?
	 */
	private static boolean isNameInUse(String name, int idOrga) throws CannotConnectToDatabaseException, QueryFailedException, SQLException {
		return DBMapper.exists(DBMapper.executeQuery(QUERY_CHECK_NAME, QueryType.SELECT, name, idOrga));
	}

	/**
	 * Is there already a server registered with this host and port in this organization. 
	 */
	private static boolean isHostAndPortInUse(String host, int port, int idOrga) throws SQLException, CannotConnectToDatabaseException, QueryFailedException {
		return DBMapper.exists(DBMapper.executeQuery(QUERY_CHECK_HOST_PORT, QueryType.SELECT, host, port, idOrga));
	}

	/**
	 * Does this server exists ?
	 */
	private static boolean serverExists(int idServer) throws SQLException, CannotConnectToDatabaseException, QueryFailedException {
		return DBMapper.exists(DBMapper.executeQuery(QUERY_GET_SERVER, QueryType.SELECT, idServer));
	}

	/**
	 * Get a server by id.
	 */
	private static Server getServer(int idServer) throws CannotConnectToDatabaseException, QueryFailedException, SQLException {
		ResultSet rs = DBMapper.executeQuery(QUERY_GET_SERVER, QueryType.SELECT, idServer);

		if (rs.next()) {
			Server result = new Server(rs.getInt(1), rs.getString(6),
					rs.getString(3), rs.getInt(4), rs.getString(5));

			return result;
		} else {
			return null;
		}

	}

	/**
	 * Does this user has the right to modify this server ?
	 */
	private static boolean canModify(int idUser, int idServer) throws SQLException, CannotConnectToDatabaseException, QueryFailedException {
		ResultSet rs = DBMapper.executeQuery(QUERY_GET_SERVER, QueryType.SELECT, idServer);

		if (!rs.next())
			return false;

		int idOrga = rs.getInt(2);

		return OrganizationUtils.hasOwnership(idUser, idOrga);
	}

	/**
	 * Does this user can access this server ?
	 */
	private static boolean canAccess(int idUser, int idServer) throws CannotConnectToDatabaseException, QueryFailedException, SQLException {
		ResultSet rs = DBMapper.executeQuery(QUERY_GET_SERVER, QueryType.SELECT, idServer);

		if (!rs.next())
			return false;

		int idOrga = rs.getInt(2);

		return OrganizationUtils.isMember(idUser, idOrga) || OrganizationUtils.hasOwnership(idUser, idOrga);
	}
}







