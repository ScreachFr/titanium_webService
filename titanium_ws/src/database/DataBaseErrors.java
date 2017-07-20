package database;

import services.errors.GenericError;
import services.errors.ServletError;

public enum DataBaseErrors implements ServletError {
	UKNOWN_SQL_ERROR(new GenericError(1001, "Unkown SQL error")),
	CANNOT_CONNECT_TO_DATABASE(new GenericError(1002, "Cannot connect to database")),
	QUERY_FAILED(new GenericError(1003, "Query execution failed")),
	UKNOWN_DB_EXCEPTION(new GenericError(1004, "Unknown database exception"));
	
	private GenericError error;
	
	private DataBaseErrors(GenericError error) {
		this.error = error;
	}

	@Override
	public int getCode() {
		return error.getCode();
	}

	@Override
	public String getMessage() {
		return error.getMessage();
	}
	
}
