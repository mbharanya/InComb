/**
 * This module contains services/controllers/... for providers.
 */
(function(ng, $) {
	var module = ng.module("provider", [
		"router"
	]);

	// define url /provider/{providerName}
	module.config(function(routerProvider) {
		routerProvider.when("provider", "/providers/:name", {
			controller: "providerCtrl",
			templateUrl: "/html/provider.html",
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
	 * This controller takes the provider with the name in the URL from the server and puts it into the scope. Afterwards it initializes the newsFilterService
	 * with the category and listens for changes on it. On a change it loads the news of this provider with the newsLoader.
	 */
	module.controller("providerCtrl", function(providersResource, newsFilterService, newsLoader, $routeParams, pageTitle, router, $scope) {
		newsFilterService.init($scope); // first init

		var loadNews = function() {
			// listen to filterChanged events.
			$scope.$on("filterChanged", function(event, filter) {
				delete $scope.news;
				newsLoader.load(filter.data, $scope);
			});
		};

		// load the provider with the name in the url.
		providersResource.get({
			name: $routeParams.name
		}).$promise.then(function(data) {
			if(data.results.length) {

				// put the provider into the scope.
				$scope.provider = data.results[0];

				// real init with providerId
				newsFilterService.init($scope, {
					providerId: $scope.provider.id
				});

				loadNews(); // register events

				// set page title
				pageTitle.change($scope.provider.name);
			}
			else {
				// if no provider was found with the requested name than forward to the not found page.
				router.go("404");
			}
		});
	});

})(angular, jQuery);
