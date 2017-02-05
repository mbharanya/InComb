package com.incomb.server.model;

import java.io.Serializable;

/**
 * This class isn't in use at the moment.
 */
public class Role implements Serializable {

	private static final long serialVersionUID = 857316279;

	private int id;
	private String nameKey;

	public Role() {
	}

	public Role(final int id, final String nameKey) {
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
