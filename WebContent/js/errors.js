/**
 * This module contains services/controllers/... for handling errors.
 */
(function(ng, $) {
	var module = ng.module("errors", [
		"router"
	]);

	/**
	 * Register error pages for status codes 404 and 500.
	 * Sets the fallback route to the 404 page.
	 * Register the 500checkInterceptor.
	 */
	module.config(function(routerProvider, $httpProvider) {
		routerProvider.when("404", "/404", {
			controller: "error404Ctrl",
			templateUrl: "/html/404.html"
		});

		routerProvider.when("500", "/500", {
			controller: "error500Ctrl",
			templateUrl: "/html/500.html"
		});

		routerProvider.otherwise({
			redirectTo: "/404"
		});

	    $httpProvider.interceptors.push("500checkInterceptor");
	});

	/**
	 * Changes the page title.
	 */
	module.controller("error404Ctrl", function(pageTitle, $translate, $scope) {

		// set page title
		$translate("pageTitles.404").then(function(title) {
			pageTitle.change(title);
		});
	});

	/**
	 * Changes the page title.
	 */
	module.controller("error500Ctrl", function(pageTitle, $translate, $scope) {

		// set page title
		$translate("pageTitles.500").then(function(title) {
			pageTitle.change(title);
		});
	});

	/**
	 * Checks every error response if it has the status code 500. If so it redirects the user
	 * to the 500 error page.
	 */
	module.service("500checkInterceptor", function($q, $log, $location) {
		return {
	        responseError: function(rejection) {
	            if (rejection.status === 500) {
	                $log.info("Response code was 500. Redirect to 500 error page. ", rejection);

	                // we can't use our router because of problems with DI.
	                $location.path("/500");
	            }

	            return $q.reject(rejection);
	        }
		};
	});


})(angular, jQuery);
