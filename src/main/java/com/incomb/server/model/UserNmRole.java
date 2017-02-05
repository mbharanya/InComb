package com.incomb.server.model;

import java.io.Serializable;

/**
 * This class isn't in use at the moment.
 */
public class UserNmRole implements Serializable {

	private static final long serialVersionUID = -202268947;

	private long userId;
	private int roleId;

	public UserNmRole() {
	}

	public UserNmRole(final long userId, final int roleId) {
		this.userId = userId;
		this.roleId = roleId;
	}

	public long getUserId() {
		return this.userId;
	}

	public void setUserId(final long userId) {
		this.userId = userId;
	}

	public int getRoleId() {
		return this.roleId;
	}

	public void setRoleId(final int roleId) {
		this.roleId = roleId;
	}
}
