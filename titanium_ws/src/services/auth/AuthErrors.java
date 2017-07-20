package services.auth;

import services.errors.GenericError;
import services.errors.ServletError;

public enum AuthErrors implements ServletError {
	//Register
	USERNAME_IN_USE(new GenericError(2001,"Username is already in use.")),
	EMAIL_IN_USE(new GenericError(2002, "Email is already in use.")),
	USERNAME_TOO_SHORT(new GenericError(2003, "Username is too short.")),
	PASSWORD_TOO_SHORT(new GenericError(2004, "Password is too short.")),
	USERNAME_TOO_LONG(new GenericError(2005, "Username is too long.")),
	PASSWORD_TOO_LONG(new GenericError(2006, "Password is too long.")),
	INVALID_EMAIL(new GenericError(2007, "Invalid email.")),
	
	// Login
	WRONG_USERNAME_OR_PASSWORD(new GenericError(1001, "Wrong username/password. The username does not exists or the password is wrong."));
	
	
	private GenericError error;
	
	private AuthErrors(GenericError error) {
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
