/**
 * This module provides services to access the REST API on the server.
 * The API will be called with a normal JavaScript function call on the service.
 * The function name will be the HTTP method of the request to send.
 *
 * @see $promise
 *
 * Example: usersResource.get();
 */
(function(ng, $) {
	/**
	 * This module contains services for all REST resources which the server provides.
	 */
	var module = ng.module("resources", [
		"ngResource"
	]);

	// prefix for all resource URIs.
	var apiPrefix = "/api";

	/**
	 * Resource for users.
	 *  - GET: queries users. Currently only search available.
	 *  - POST: create a new user (registration).
	 */
	module.service("usersResource", ["$resource", function($resource) {
		return $resource(apiPrefix + "/users", {}, {
			get: {
				method: "get"
			},
			post: {
				method: "post"
			}
		});
	}]);

	/**
	 * Resource user settings
	 *  - GET: current information of user
	 *  - PUT: Update information of user
	 */
	module.service("userResource", ["$resource", function($resource) {
		return $resource(apiPrefix + "/users/:userId", {
			"userId": "@userId"
		}, {
			get: {
				method: "get"
			},
			put: {
				method: "put"
			}
		});
	}]);


	/**
	 * Resource for the tag preferences of a user.
	 *  - GET: get all tags prefered by given user.
	 *  - POST: adds a new tag
	 */
	module.service("tagPreferencesResource", ["$resource", function($resource) {
		return $resource(apiPrefix + "/users/:userId/tags", {
			"userId": "@userId"
		}, {
			get: {
				method: "get",
				isArray: true
			},
			post: {
				method: "post"
			}
		});
	}]);

	/**
	 * Resource for a tag preference of a user.
	 *  - DELETE: removes a tag
	 */
	module.service("tagPreferenceResource", ["$resource", function($resource) {
		return $resource(apiPrefix + "/users/:userId/tags/:tag", {
			"userId": "@userId",
			"tag": "@tag"
		}, {
			delete: {
				method: "delete"
			}
		})
	}]);

	/**
	 * Resource for the category preferences of a user.
	 *  - GET: get all categoryies with information about the preference for the user.
	 */
	module.service("categoryPreferencesResource", ["$resource", function($resource) {
		return $resource(apiPrefix + "/users/:userId/categories", {
			"userId": "@userId"
		}, {
			get: {
				method: "get",
				isArray: true
			},
			put: {
				method: "put"
			}
		});
	}]);

	/**
	 * Resource for a category preference of a user.
	 *  - PUT: change factor.
	 */
	module.service("categoryPreferenceResource", ["$resource", function($resource) {
		return $resource(apiPrefix + "/users/:userId/categories/:categoryId", {
			"userId": "@userId",
			"categoryId": "@categoryId"
		}, {
			put: {
				method: "put"
			}
		});
	}]);

	/**
	 * Resource for the currently logged in user.
	 *  - GET: get information about the current logged in user
	 *  - POST: try to login a user
	 *  - DELETE: logout a user
	 */
	module.service("loggedInUserResource", ["$resource", function($resource) {
		return $resource(apiPrefix + "/users/loggedIn", {}, {
			get: {
				method: "get"
			},
			post: {
				method: "post"
			},
			delete: {
				method: "delete"
			}
		});
	}]);

	/**
	 * Resource for the ins of a specific user.
	 *  - GET: ins for the given user.
	 */
	module.service("userInsResource", ["$resource", function($resource) {
		return $resource(apiPrefix + "/users/:userId/ins", {
			"userId": "@userId"
		}, {
			get: {
				method: "get"
			}
		});
	}]);

	/**
	 * Resource for the combs of a specific user.
	 *   - GET: combs for the given user.
	 */
	module.service("userCombsResource", ["$resource", function($resource) {
		return $resource(apiPrefix + "/users/:userId/combs", {
			"userId": "@userId"
		}, {
			get: {
				method: "get"
			}
		});
	}]);

	/**
	 * Resource for the comments of a specific user.
	 *   - GET: comments for the given user.
	 */
	module.service("userCommentsResource", ["$resource", function($resource) {
		return $resource(apiPrefix + "/users/:userId/comments", {
			"userId": "@userId"
		}, {
			get: {
				method: "get"
			}
		});
	}]);

	/**
	 * Resource for categories.
	 *  - GET: gets all categories or all for a given moduleId (GET-Param).
	 *  - SEARCH: actually a GET, used for searching.
	 */
	module.service("categoriesResource", ["$resource", function($resource) {
		return $resource(apiPrefix + "/categories", {}, {
			get: {
				method: "get"
			}
		});
	}]);

	/**
	 * Resource for providers.
	 *  - GET: search for providers.
	 */
	module.service("providersResource", ["$resource", function($resource) {
		return $resource(apiPrefix + "/providers", {}, {
			get: {
				method: "get"
			}
		});
	}]);

	/**
	 * Resource for news.
	 *  - GET: get news.
	 *  - SEARCH: actually a GET, used for searching.
	 */
	module.service("newsResource", ["$resource", function($resource) {
		return $resource(apiPrefix + "/news", {}, {
			get: {
				method: "get"
			}
		});
	}]);

	/**
	 * Resource for one news.
	 *  - GET: gets a specific news by id.
	 */
	module.service("newsRecResource", ["$resource", function($resource) {
		return $resource(apiPrefix + "/news/:id", {
			"id": "@id"
		}, {
			get: {
				method: "get"
			}
		});
	}]);

	/**
	 * Resource for ins.
	 *  - GET: gets a specific in by contentId.
	 */
	module.service("insResource", ["$resource", function($resource) {
		return $resource(apiPrefix + "/news/:contentId/ins", {
			"contentId": "@contentId"
		}, {
			get: {
				method: "get"
			}
		});
	}]);

	/**
	 * Resource for combs.
	 *  - GET: gets a specific comb by contentId.
	 */
	module.service("combsResource", ["$resource", function($resource) {
		return $resource(apiPrefix + "/news/:contentId/combs", {
			"contentId": "@contentId"
		}, {
			get: {
				method: "get"
			}
		});
	}]);


	/**
	 * Resource for votes.
	 *  - POST: posts by contentId.
	 */
	module.service("votesResource", ["$resource", function($resource) {
		return $resource(apiPrefix + "/news/:contentId/votes", {
			"contentId": "@contentId"
		}, {
			post: {
				method: "post"
			}
		});
	}]);


	/**
	 * Resource for comments.
	 *  - GET: gets a specific comment by contentId.
	 *  - POST: posts by contentId.
	 */
	module.service("commentsResource", ["$resource", function($resource) {
		return $resource(apiPrefix + "/news/:contentId/comments", {
			"contentId": "@contentId"
		}, {
			get: {
				method: "get"
			},
			post: {
				method: "post"
			}
		});
	}]);

	/**
	 * Resource for comb items.
	 *  - GET: gets unread comb items for a user
	 */
	module.service("combItemsResource", ["$resource", function($resource) {
		return $resource(apiPrefix + "/users/:userId/combItems", {
			"userId": "@userId"
		}, {
			get: {
				method: "get",
				isArray: true
			}
		});
	}]);

	/**
	 * Resource for comb items.
	 *  - PUT: changes a comb item
	 *  - POST: adds a new comb item for the user.
	 *  - DELETE: removes the comb item.
	 */
	module.service("combItemResource", ["$resource", function($resource) {
		return $resource(apiPrefix + "/users/:userId/combItems/:contentId", {
			"userId": "@userId",
			"contentId": "@contentId"
		}, {
			put: {
				method: "put"
			},
			post: {
				method: "post"
			},
			delete: {
				method: "delete"
			}
		});
	}]);

    /**
	 * Resource for flyWith
	 *  - POST:   Adds a new FlyWith to the given user.
	 *  - DELETE: Removes a new FlyWith to the given user.
	 */
	module.service("flyWithResource", ["$resource", function($resource) {
		return $resource(apiPrefix + "/users/:userId/flyWiths/:flyWithId", {
			"userId":    "@userId",
            "flyWithId": "@flyWithId"
		}, {
			post: {
				method: "post"
			},
			delete: {
				method: "delete"
			}
		});
	}]);

	/**
	 * Resource for locales of a user.
	 *  - GET:   Returns all existing user locales
	 *  - POST:	 Adds a new user locale
	 */
	module.service("userLocalesResource", ["$resource", function($resource) {
		return $resource(apiPrefix + "/users/:userId/locales", {
			"userId": "@userId"
		}, {
			get: {
				method: "get",
				isArray: true
			},
			post: {
				method: "post"
			}
		});
	}]);

	/**
	 * Resource for a locale of a user.
	 *  - DELETE: Removes a user locale
	 */
	module.service("userLocaleResource", ["$resource", function($resource) {
		return $resource(apiPrefix + "/users/:userId/locales/:locale", {
			"userId": "@userId",
			"locale": "locale"
		}, {
			delete: {
				method: "delete"
			}
		});
	}]);

	/**
	 * Resource for locales.
	 *  - GET:   Returns all available locales
	 */
	module.service("localesResource", ["$resource", function($resource) {
		return $resource(apiPrefix + "/locales", {}, {
			get: {
				method: "get",
				isArray: true
			}
		});
	}]);

})(angular, jQuery);
