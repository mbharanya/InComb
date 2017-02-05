package com.incomb.server.model;

import java.io.Serializable;

/**
 * This class isn't in use at the moment.
 */
public class RoleNmPermission implements Serializable {

	private static final long serialVersionUID = -1527153179;

	private int roleId;
	private int permissionId;

	public RoleNmPermission() {
	}

	public RoleNmPermission(final int roleId, final int permissionId) {
		this.roleId = roleId;
		this.permissionId = permissionId;
	}

	public int getRoleId() {
		return this.roleId;
	}

	public void setRoleId(final int roleId) {
		this.roleId = roleId;
	}

	public int getPermissionId() {
		return this.permissionId;
	}

	public void setPermissionId(final int permissionId) {
		this.permissionId = permissionId;
	}
}
