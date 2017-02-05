/**
 * This module contains services/controllers/... for categories.
 */
(function(ng, $) {
	var module = ng.module("category", [
		"router"
	]);

	// define url /categories/{categoryName}
	module.config(function(routerProvider) {
		routerProvider.when("category", "/categories/:name", {
			controller: "categoryCtrl",
			templateUrl: "/html/category.html",
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
	 * This controller takes the category with the name in the URL from the server and puts it into the scope. Afterwards it initializes the newsFilterService
	 * with the category and listens for changes on it. On a change it loads the news of this category with the newsLoader.
	 */
	module.controller("categoryCtrl", function(categoriesResource, newsLoader, newsFilterService, $routeParams, $translate, pageTitle, localeUtil, router, $scope) {
		newsFilterService.init($scope);

		var loadNews = function() {
			// listen to filterChanged events.
			$scope.$on("filterChanged", function(event, filter) {
				delete $scope.news; // remove old news
				newsLoader.load(filter.data, $scope); // take new one
			});
		};

		// load the category with the name in the url.
		categoriesResource.get({
			search: $routeParams.name,
			locale: localeUtil.getCurrent(),
			amount: 1
		}).$promise.then(function(data) {
			if(data.results.length) {
				// put the category into the scope.
				$scope.category = data.results[0];

				// initialize the newsFilterService with the category as prefilled value.
				newsFilterService.init($scope, {
					categoryId: $scope.category.id
				});

				loadNews(); // register events

				// change the page title.
				$translate($scope.category.nameKey).then(function(title) {
					pageTitle.change(title);
				});
			}
			else {
				// if no category was found with the requested name than forward to the not found page.
				router.go("404");
			}
		});
	});

})(angular, jQuery);
