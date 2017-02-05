package com.incomb.server.services.validators.rulesets;

import com.incomb.server.services.validators.RequiredValidator;
/**
 * Contains common validators for Votes with accoring failure messages
 * All failure messages must be localized in the {langcode}.json
 */
public class VoteRuleSet extends ARuleSet{
	/**
	 * Construct common validators for votes
	 */
	public VoteRuleSet() {
		addRule("userId", new RequiredValidator(), "validation.votes.userId.required");
		addRule("contentId", new RequiredValidator(), "validation.votes.contentId.required");
		addRule("up", new RequiredValidator(), "validation.votes.up.required");
	}
}
