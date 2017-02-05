package com.incomb.server.services.validators;
/**
 * Interface for {@link AValidator}, any new Validators have to extend {@link AValidator}.
 *
 * @param <T> The type of the field to validate
 */
public interface IValidator<T> {
	boolean isValid(T field);
}
