package services.servers;

import services.errors.GenericError;
import services.errors.ServletError;

public enum ServerErrors implements ServletError{
	DUPLICATED_SERVER(new GenericError(4001, "Duplicated server.")),
	DUPLICATED_SERVER_NAME(new GenericError(4002, "Duplicated server name.")),
	CANNOT_FIND_SERVER(new GenericError(4003, "Cannot find server.")),
	CANNOT_CONNECT(new GenericError(4004, "Cannot connect to server."));
	
	private GenericError error;
	
	private ServerErrors(GenericError error) {
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
