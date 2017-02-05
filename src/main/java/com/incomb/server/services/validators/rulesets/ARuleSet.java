package com.incomb.server.services.validators.rulesets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.incomb.server.services.validators.IValidator;
import com.incomb.server.services.validators.ValidationInfo;

public abstract class ARuleSet implements IRuleSet {
	/**
	 * contains all rules, which have been added by {@link #addRule(String, IValidator, String)}
	 */
	private final Map<String, List<ValidationInfo<?>>> rules = new HashMap<>();

	/**
	 * Add a new rule for fieldName
	 * @param fieldName the fieldName to validate
	 * @param validator to use for the Validation
	 * @param errorMessage localized error message string defined in {langcode}.json
	 */
	protected void addRule(final String fieldName, final IValidator<?> validator, final String errorMessage) {
		List<ValidationInfo<?>> validationInfos = rules.get(fieldName);
		if (validationInfos == null){
			validationInfos = new ArrayList<>();
			rules.put(fieldName, validationInfos);
		}
		validationInfos.add(new ValidationInfo(validator, errorMessage));
	}

	@Override
	/**
	 * Returns all current Rules
	 */
	public Map<String, List<ValidationInfo<?>>> getRules() {
		return rules;
	}
}
