/**
 * This module provides the functionality for the search.
 */
(function(ng, $) {
	var module = ng.module("search", [
		"resources"
	]);

	/**
	 * This controller initializes the search and executes it. If the user writes into the search field
	 * requests are made to the API with the services for each result type. (users, news, providers, categories).
	 */
	module.controller("searchCtrl", function($scope, userSearch, categorySearch, providerSearch, newsSearch) {

		// the search text of the user will be in this model
		$scope.searchText = "";

		/**
		 * Removes the search text.
		 */
		$scope.cancelSearch = function() {
			$scope.searchText = "";
		};

		// executes the search for a result type.
		var doSearch = function(resource, scopeProp, amount) {
			resource($scope.searchText, amount, function(data) {
				$scope.results[scopeProp] = data;
				$scope.results[scopeProp].hasMore = amount < $scope.results[scopeProp].totalHits;
				$scope.results[scopeProp].loadMore = function() {
					doSearch(resource, scopeProp, amount + 3);
				};
			});
		};

		// listen for search text changes.
		$scope.$watch("searchText", function() {
			if($scope.searchText.length) {
				// search each result type.
				$scope.searchActive = true;

				$scope.results = {};
				doSearch(userSearch, "users", 3);
				doSearch(categorySearch, "categories", 3);
				doSearch(providerSearch, "providers", 3);
				doSearch(newsSearch, "news", 3);
			}
			else {

				// if the search text is empty then remove all existing results and deactivate the search.
				$scope.searchActive = false;
				$scope.results = null;
			}
		});

		// if the user changes the page then cancel the search.
		$scope.$on("$locationChangeStart", function() {
			$scope.cancelSearch();
		});
	});

	/**
	 * This service calls the resource for user results.
	 */
	module.service("userSearch", function(usersResource) {
		return function(searchText, amount, callback) {
			usersResource.get({
				search: searchText,
				amount: amount
			}).$promise.then(callback);
		};
	});

	/**
	 * This service calls the resource for category results.
	 */
	module.service("categorySearch", function(categoriesResource, localeUtil) {
		return function(searchText, amount, callback) {
			categoriesResource.get({
				search: searchText,
				amount: amount,
				locale: localeUtil.getCurrent()
			}).$promise.then(callback);
		};
	});

	/**
	 * This service calls the resource for provider results.
	 */
	module.service("providerSearch", function(providersResource, localeUtil) {
		return function(searchText, amount, callback) {
			providersResource.get({
				search: searchText,
				amount: amount,
				locale: localeUtil.getCurrent()
			}).$promise.then(callback);
		};
	});

	/**
	 * This service calls the resource for news results.
	 */
	module.service("newsSearch", function(newsResource, localeUtil) {
		return function(searchText, amount, callback) {
			newsResource.get({
				searchText: searchText,
				amount: amount,
				locale: localeUtil.getCurrent(),
				groupNews: false
			}).$promise.then(callback);
		};
	});

})(angular, jQuery);
