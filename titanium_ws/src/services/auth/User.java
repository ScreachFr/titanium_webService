package services.auth;

public class User {
	private int id;
	private String username;
	private String email;
	
	public User(int id, String username, String email) {
		this.id = id;
		this.username = username;
		this.email = email;
	}
	
	public int getId() {
		return id;
	}
	
	public String getUsername() {
		return username;
	}
	
	public String getEmail() {
		return email;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof User) {
			User other = (User) obj;
			return id == other.getId();
		} else {
			return super.equals(obj);
		}
		
	}
}
