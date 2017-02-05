package com.incomb.server.services;

/**
 * Used for Responses to send to the client, usually in a JSON format
 *
 */
public class ResponseBean {
	private String message;
	private Boolean success;
	private Object model;

	public ResponseBean(final String message, final Boolean success) {
		super();
		this.message = message;
		this.success = success;
	}

	public ResponseBean(final String message, final Boolean success, final Object model) {
		this(message, success);
		this.model = model;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(final String message) {
		this.message = message;
	}

	public Boolean getSuccess() {
		return success;
	}

	public void setSuccess(final Boolean success) {
		this.success = success;
	}

	public Object getModel() {
		return model;
	}

	public void setModel(final Object model) {
		this.model = model;
	}
}