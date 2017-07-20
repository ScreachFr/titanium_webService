package services.organization;

import services.auth.User;

public class Organization {
	private int id;
	private String name;
	private User owner;
	
	public Organization(int id, String name, User owner) {
		this.id = id;
		this.name = name;
		this.owner = owner;
	}
	
	
	public int getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public User getOwner() {
		return owner;
	}
}
