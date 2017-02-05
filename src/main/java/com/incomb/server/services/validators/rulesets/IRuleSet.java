package com.incomb.server.services.validators.rulesets;

import java.util.List;
import java.util.Map;

import com.incomb.server.services.validators.ValidationInfo;

/**
 * Interface for {@link ARuleSet}
 */
public interface IRuleSet{
	Map<String, List<ValidationInfo<?>>> getRules();
}
