package com.incomb.server.services.validators.rulesets;

import com.incomb.server.model.dao.IRecordExistsChecker;
import com.incomb.server.services.validators.DisplayNameValidator;
import com.incomb.server.services.validators.DuplicateValidator;
import com.incomb.server.services.validators.EmailValidator;
import com.incomb.server.services.validators.PasswordValidator;
import com.incomb.server.services.validators.RequiredValidator;
import com.incomb.server.services.validators.UsernameValidator;
/**
 * Contains common validators for Votes with accoring failure messages
 * All failure messages must be localized in the {langcode}.json
 */
public class UserRegisterRuleSet extends ARuleSet{

	/**
	 * Sets validation rules for a user registration<br />
	 * @param dao the DAO to use for the {@link DuplicateValidator}
	 */
	public UserRegisterRuleSet(final IRecordExistsChecker dao) {
		addRule("email", new RequiredValidator(), "validation.user.email.required");
		addRule("email", new EmailValidator(), "validation.user.email.invalid");

		addRule("username", new DuplicateValidator(dao, "username"), "validation.user.username.exists");
		addRule("username", new RequiredValidator(), "validation.user.username.required");
		addRule("username", new UsernameValidator(), "validation.user.username.invalid");

		addRule("displayName", new RequiredValidator(), "validation.user.displayName.required");
		addRule("displayName", new DisplayNameValidator(), "validation.user.displayName.invalid");

		addRule("password", new RequiredValidator(), "validation.user.password.required");
		addRule("password", new PasswordValidator(), "validation.user.password.invalid");
	}
}
