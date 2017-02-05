/**
 * This module is an extension for angular's default router.
 * This router can generate URL.
 */
(function(angular, $) {

	var module = angular.module("router", [
		"ngRoute"
	]);

	module.provider("router", function($routeProvider) {
		var router = this;

		var routes = {};

		/**
		 * Creates a new route with the given identifying name and the url pattern.
		 * @param name string: an identifying name for this route. Can be used to generate urls.
		 * @param url string: the url pattern. See at $routeProvider.
		 * @param opts object|undefined: the route configuration used by $routeProvider.
		 */
		router.when = function(name, pattern, opts) {
			routes[name] = {
				name: name,
				pattern: pattern,
				opts: opts
			};

			$routeProvider.when(pattern, opts);
		};

		/**
		 * Fallback if no route matched. For further information read $routeProvider.
		 * This function delegates the options directly to the $routeProvider.
		 * @param opts object|undefined: the configuration used by $routeProvider.
		 */
		router.otherwise = function(opts) {
			$routeProvider.otherwise(opts);
		};

		router.$get = function($route, $location, $routeParams, $window, $log) {
			var defaultOpts = {
				inheritUrlParams: true,		// if true, the $routeParams of the current route will be used to fill the placeholders in the pattern
				inheritQueryParams: false	// if true, the current query params will be appended to the query string
			};

			/**
			 * Generates the absolute URL for the route.
			 * Placeholders without a given value will be removed from the URL.
			 * Unnecessary slashes will be removed.
			 *
			 * @param name string: the identifying name of the route.
			 * @param urlParams object|undefined: parameters for the placeholders in the URL.
			 * @param queryParams object|undefined: parameters which will be appended to the query string
			 * @param opts object|undefined: see defaultOpts
			 * @return string: the generated absolute URL.
			 * @throws Error if no route with the given name was found.
			 */
			var url = function(name, urlParams, queryParams, opts) {
				urlParams = urlParams || {};
				queryParams = queryParams || {};
				opts = angular.extend({}, defaultOpts, opts || {});

				var route = routes[name];
				if(!route) {
					throw new Error( "Can't generate url for unknown route '" + name + "'." );
				}

				if(opts.inheritUrlParams) {
					urlParams = angular.extend({}, $routeParams, urlParams);
				}

				if(opts.inheritQueryParams) {
					queryParams = angular.extend({}, $location.search(), queryParams);
				}

				var url = route.pattern.replace(/:[a-z0-9]+/ig, function(match) {
					var param = match.substring(1); // remove :
					return $window.encodeURIComponent(urlParams[param] || ""); // if param is not defined -> remove it.
				});

				url.replace(/\/{2,}/g, "/"); // remove multiple slashes

				if(angular.isObject(queryParams) && Object.keys(queryParams).length) {
					url += "?" + $.param(queryParams);
				}

				return url;
			};

			/**
			 * Generates the URL for the given name and goes to it.
			 * For more information about the arguments look at #url.
			 */
			var go = function(name, urlParams, queryParams, opts) {
				$location.url(url(name, urlParams, queryParams, opts));
			};

			/**
			 * Reloads the current route.
			 */
			var reload = function() {
				$route.reload();
			};

			return {
				url: url,
				go: go,
				reload: reload
			};
		};
	});

	/**
	 * Filter to generate absolute urls.
	 * For more information about the arguments look at #url in the provider.
	 */
	module.filter("url", function(router) {
		return function(name, urlParams, queryParams, opts) {
			return router.url(name, urlParams, queryParams, opts);
		}
	});

	/**
	 * Filter to check if the given route is the current route.
	 */
	module.filter("activeRoute", function(router, $location) {
		return function(name, urlParams, queryParams, opts) {
			var routeUrl = router.url(name, urlParams, queryParams, angular.extend({
				inheritQueryParams: true
			}, opts));
			return $location.url() == routeUrl;
		}
	});

})(angular, jQuery);