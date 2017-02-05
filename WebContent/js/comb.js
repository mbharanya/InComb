/**
 * This module contains services/controllers/... for my comb.
 */
(function(ng, $) {
	var module = ng.module("comb", [
		"router"
	]);

	// define url /comb
	module.config(function(routerProvider) {
		routerProvider.when("comb", "/comb", {
			controller: "combCtrl",
			templateUrl: "/html/comb.html",
			resolve: {
				// wait till the current logged in user is loaded.
				user: function(getUser) {
					return getUser(true);
				}
			}
		});
	});

	/**
	 * This controller loads all comb items (news) of the current logged in user and prepares them with the newsLoader.
	 * Afterwards it puts them into the scope.
	 */
	module.controller("combCtrl", function(combItemsResource, newsLoader, $translate, pageTitle, router, $scope, user) {
		// set page title
		$translate("pageTitles.myComb").then(function(title) {
			pageTitle.change(title);
		});

		// load the news from the server.
		combItemsResource.get({
			userId: user.id
		}).$promise.then(function(data) {
			// prepare each news
			ng.forEach(data, function(news) {
				newsLoader.prepare(news, $scope)
			});

			// put the news to the scope.
			$scope.news = data;
		});
	});

})(angular, jQuery);
