package com.incomb.server.services.validators.rulesets;

import com.incomb.server.services.validators.CommentValidator;
import com.incomb.server.services.validators.RequiredValidator;
/**
 * Contains common validators for Comments with accoring failure messages
 * All failure messages must be localized in the {langcode}.json
 */
public class CommentRuleSet extends ARuleSet{

	/**
	 * Constructs a set of validators to validate comments
	 */
	public CommentRuleSet() {
		super();
		addRule("userId", new RequiredValidator(), "validation.comment.userId.required");
		addRule("contentId", new RequiredValidator(), "validation.comment.contentId.required");
		addRule("comment", new RequiredValidator(), "validation.comment.comment.required");
		addRule("comment", new CommentValidator(), "validation.comment.comment.invalid");
	}
}
