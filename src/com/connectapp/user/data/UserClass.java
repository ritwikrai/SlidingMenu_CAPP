package com.connectapp.user.data;

import java.io.Serializable;

public class UserClass implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String name = "";
	private String userId = "";
	private String databaseId = "";
	private String phone = "";
	private String email = "";
	private String status = "";
	private String userName = "";
	private String password = "";
	private boolean isRemember = false;
	private boolean isLoggedin = false;
	private String message = "";
	private int memberCount=0;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getDatabaseId() {
		return databaseId;
	}

	public void setDatabaseId(String databaseId) {
		this.databaseId = databaseId;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean getIsRemember() {
		return isRemember;
	}

	public void setIsRemember(boolean isRemember) {
		this.isRemember = isRemember;
	}

	public boolean getIsLoggedin() {
		return isLoggedin;
	}

	public void setIsLoggedin(boolean isLoggedin) {
		this.isLoggedin = isLoggedin;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public int getMemberCount() {
		return memberCount;
	}

	public void setMemberCount(int memberCount) {
		this.memberCount = memberCount;
	}

}
