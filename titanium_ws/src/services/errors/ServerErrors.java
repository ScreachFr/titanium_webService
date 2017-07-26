package services.errors;

public enum ServerErrors implements ServletError {
	MISSING_ARGUMENT(new GenericError(1001, "Missing argument(s)")),
	BAD_ARGUMENT(new GenericError(1002, "Invalid argument(s)")),
	INVALID_KEY(new GenericError(1003, "Invalid key"));
	
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
