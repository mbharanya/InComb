package com.incomb.server.services.validators.rulesets;

import com.incomb.server.services.validators.DisplayNameValidator;
import com.incomb.server.services.validators.EmailValidator;
import com.incomb.server.services.validators.PasswordValidator;
import com.incomb.server.services.validators.UsernameValidator;
/**
 * Contains common validators for Updates of Userdata with accoring failure messages
 * All failure messages must be localized in the {langcode}.json
 */
public class UserUpdateRuleSet extends ARuleSet{

	/**
	 * Sets validation rules for a user update<br />
	 */
	public UserUpdateRuleSet() {
		addRule("email", new EmailValidator(), "validation.user.email.invalid");
		addRule("username", new UsernameValidator(), "validation.user.username.invalid");
		addRule("displayName", new DisplayNameValidator(), "validation.user.displayName.invalid");
		addRule("password", new PasswordValidator(), "validation.user.password.invalid");
	}
}
