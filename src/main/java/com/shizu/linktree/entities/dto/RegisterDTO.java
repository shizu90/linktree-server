package com.shizu.linktree.entities.dto;

public class RegisterDTO {
	private String email;
	private String password;
	private String username;
	private String confirmPassword;
	
	public RegisterDTO() {}
	public RegisterDTO(String email, String password, String username, String confirmPassword) {
		this.email = email;
		this.password = password;
		this.confirmPassword = confirmPassword;
		this.username = username;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getConfirmPassword() {
		return confirmPassword;
	}
	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}
}
