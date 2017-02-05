/**
 * This module provides the controller for the settings page.
 */
(function(ng, $) {
	var module = ng.module("settings", [
		"router",
		"resources"
	]);

	// define url /settings
	module.config(function(routerProvider) {
		routerProvider.when("settings", "/settings", {
			controller: "settingsCtrl",
			templateUrl: "/html/settings.html",
			resolve: {
				// wait till the current logged in user is loaded.
				user: function(getUser) {
					return getUser(true);
				}
			}
		});
	});

	/**
	 * The controller changes the page title.
	 */
	module.controller("settingsCtrl", function($translate, pageTitle) {
		$translate("pageTitles.settings").then(function(title) {
			pageTitle.change(title);
		});
	});

})(angular, jQuery);
