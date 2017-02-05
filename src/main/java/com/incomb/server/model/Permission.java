package com.incomb.server.model;

import java.io.Serializable;

/**
 * This class isn't in use at the moment.
 */
public class Permission implements Serializable {

	private static final long serialVersionUID = -1546743056;

	private int id;
	private String nameKey;

	public Permission() {
	}

	public Permission(final int id, final String nameKey) {
		this.id = id;
		this.nameKey = nameKey;
	}

	public int getId() {
		return this.id;
	}

	public void setId(final int id) {
		this.id = id;
	}

	public String getNameKey() {
		return this.nameKey;
	}

	public void setNameKey(final String nameKey) {
		this.nameKey = nameKey;
	}
}
