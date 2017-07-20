package services.organization;

import services.errors.GenericError;
import services.errors.ServletError;

public enum OrgaErrors implements ServletError {
	NAME_IN_USE(new GenericError(3001, "This name is already in use.")),
	OWNERSHIP_REQ(new GenericError(3002, "Permission denied (ownership required).")),
	MEMBERSHIP_REQ(new GenericError(3003, "Permission denied (membership required).")),
	PERMISSION_DENIED(new GenericError(3004, "Permission denied.")),
	UKN_ORGA(new GenericError(3005, "Can't find requested organization.")),
	ALREADY_MEMBER(new GenericError(3006, "User already member of this organization.")),
	UKN_USER(new GenericError(3007, "Uknown user."));

	private GenericError error;
	
	private OrgaErrors(GenericError error) {
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
