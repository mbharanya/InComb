package com.incomb.server.services.validators;
/**
 * Used to give information about the Validation for a specific field
 * @param <T> The type of the Field
 */
public class ValidationInfo<T> {
	final IValidator<T> validator;
	final String message;
	
	/**
	 * @param validator to use for the field
	 * @param message if the validation fails
	 */
	public ValidationInfo(final IValidator<T> validator, final String message) {
		super();
		this.validator = validator;
		this.message = message;
	}
}