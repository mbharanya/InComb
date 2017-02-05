package com.incomb.server.services.validators;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.incomb.server.services.users.exceptions.ValidationException;
import com.incomb.server.services.validators.rulesets.IRuleSet;

/**
 * Validates POJOs with  and returns a {@link ValidationResponseBean} after validation
 * @see ValidationResponseBean
 * @see ValidationException
 */
public class ValidationManager {
	private final Object toValidate;
	private final Map<String, List<ValidationInfo<?>>> rules = new HashMap<>();
	private final ValidationResponseBean responseBean = new ValidationResponseBean();

	/**
	 * Sets the Object to validate
	 * @param toValidate
	 */
	public ValidationManager(final Object toValidate) {
		this.toValidate = toValidate;
	}

	
	/**
	 * Adds a new ruleset to the current one
	 * @param ruleSet
	 */
	public void addRuleSet(final IRuleSet ruleSet){
		rules.putAll(ruleSet.getRules());
	}
	
	/**
	 * Add a new rule for fieldName
	 * @param fieldName
	 * @param validator
	 * @param errorMessage
	 */
	public void addRule(final String fieldName, final IValidator<?> validator, final String errorMessage) {
		List<ValidationInfo<?>> validationInfos = rules.get(fieldName);
		if (validationInfos == null){
			validationInfos = new ArrayList<>();
			rules.put(fieldName, validationInfos);
		}
		validationInfos.add(new ValidationInfo(validator, errorMessage));
	}
	
	/**
	 * Validates all fields in the Ruleset and fills a {@link ValidationResponseBean}
	 * @return a {@link ValidationResponseBean}
	 * @throws ValidationException if an unexpected error during the Validation occurs
	 */
	public ValidationResponseBean getValidatedResponse() throws ValidationException {
		responseBean.setSuccess(true); // TODO: wrong way around?
		final List<Field> fields = new ArrayList<>(Arrays.asList(toValidate.getClass().getSuperclass().getDeclaredFields()));
		fields.addAll(Arrays.asList(toValidate.getClass().getDeclaredFields()));		
		
		// loop through fields of toValidate
		for (final Field field : fields) {
			// if field is in rules use Validator to validate field
			final String key = field.getName();
			if (rules.containsKey(key)) {
				for (final ValidationInfo info : rules.get(key)) {
					try {						
						final boolean valid = info.validator.isValid(getGetterOfField(field).invoke(toValidate));
						if (valid) {
							responseBean.addField(key, responseBean.new FieldValidation(valid));
						}else{
							responseBean.addField(key, responseBean.new FieldValidation(valid, info.message)); // add message only if not valid
							responseBean.setSuccess(false);
						}
					} catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException | NullPointerException e) {
						throw new ValidationException("Error validating Field", e);
					}
				}
			}
		}
		return responseBean;
	}
	
	/**
	 * Returns a getter from a {@link Field}
	 * @param field
	 * @return the found getter {@link Method}
	 */
	private Method getGetterOfField(final Field field){
		final Class<?> clazz = field.getDeclaringClass();
		
		for (final Method method: clazz.getMethods()){
			String fieldName = field.getName();
			// turn first character to uppercase
			fieldName = Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1); 
			//  check if method is getter of field
			if (method.getName().equals("get"+fieldName) || method.getName().equals("is"+fieldName)){
				return method;
			}
		}
		return null;
	}
}
