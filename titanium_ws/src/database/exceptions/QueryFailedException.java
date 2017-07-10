package database.exceptions;

import java.sql.SQLException;

public class QueryFailedException extends Exception {
	private static final long serialVersionUID = 6918951905251057140L; 

	private SQLException ex;
	
	public QueryFailedException(SQLException ex) {
		this.ex = ex;
	}
	
	public SQLException getSQLException() {
		return ex;
	}
	
	
}
