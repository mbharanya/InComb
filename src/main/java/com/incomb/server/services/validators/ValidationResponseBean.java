package com.incomb.server.services.validators;

import java.util.HashMap;
import java.util.Map;
/**
 * This bean is sent to the client to show Validation success or errors
 */
public class ValidationResponseBean {
	private Map<String, FieldValidation> fields = new HashMap<>();
	private boolean success;

	public ValidationResponseBean() {
		super();
	}

	public ValidationResponseBean(final Map<String, FieldValidation> fields, final boolean success) {
		super();
		this.fields = fields;
		this.success = success;
	}

	public Map<String, FieldValidation> getFields() {
		return fields;
	}

	public void setFields(final Map<String, FieldValidation> fields) {
		this.fields = fields;
	}

	public boolean getSuccess() {
		return success;
	}

	public void setSuccess(final boolean success) {
		this.success = success;
	}

	public void addField(final String key, final FieldValidation fieldValidation) {
		if (!fields.containsKey(key) || fields.get(key).isValid()){
			fields.put(key, fieldValidation);
		}
	}

	/**
	 * Used to contain message for invalid fields
	 */
	public class FieldValidation {
		private boolean valid;
		private String message;

		public FieldValidation(final boolean valid, final String message) {
			super();
			this.valid = valid;
			this.message = message;
		}

		public FieldValidation(final boolean valid) {
			super();
			this.valid = valid;
		}

		public boolean isValid() {
			return valid;
		}

		public void setValid(final boolean valid) {
			this.valid = valid;
		}

		public String getMessage() {
			return message;
		}

		public void setMessage(final String message) {
			this.message = message;
		}
	}

}