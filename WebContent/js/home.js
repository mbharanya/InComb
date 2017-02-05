/**
 * This module contains services/controllers/... for the home view.
 */
(function(ng, $) {
	var module = ng.module("home", [
		"resources",
		"router"
	]);

	// define url /
	module.config(function(routerProvider) {
		routerProvider.when("home", "/", {
			controller: "homeCtrl",
			templateUrl: "/html/home.html",
			reloadOnSearch: false,
			resolve: {
				// wait till the current logged in user is loaded.
				user: function(getUser) {
					return getUser(false);
				}
			}
		});
	});

	/**
	 * Changes the page title and loads news from the newsLoader when the filter has changed.
	 */
	module.controller("homeCtrl", function(newsLoader, $translate, pageTitle, $scope) {

		// set page title
		$translate("pageTitles.home").then(function(title) {
			pageTitle.change(title);
		});

		// listen for filterChanged events.
		$scope.$on("filterChanged", function(event, filter) {
			delete $scope.news; // remove old news and load new ones.
			newsLoader.load(filter.data, $scope);
		});
	});

})(angular, jQuery);
