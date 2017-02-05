/**
 * This module contains services/controllers/... for the news filter.
 */
(function(ng, $) {
	var module = ng.module("filter", [
		"resources"
	]);

	/**
	 * This controller puts all categories and providers into the scope to prefill the filters.
	 * It emits filterChanged events when a filter value has changed.
	 */
	module.controller("newsFilterCtrl", function(newsFilterService, providersResource, categoriesResource, $log, $location, $scope) {
		newsFilterService.init($scope);

		// load the providers and put them into the scope.
		providersResource.get().$promise.then(function(data) {
			$scope.providers = data.results;
		});

		// load the categories and put them into the scope.
		categoriesResource.get({
			"moduleId": 1
		}).$promise.then(function(data) {
			$scope.categories = data.results;
		});

		// Emit filterChanged events
		$scope.$watchCollection("filter.data", function() {
			$log.info("New news filter params.", $scope.filter);
			$location.search($scope.filter.data);
			$scope.$emit("filterChanged", $scope.filter);
		});
	});

	/**
	 * This service can be used to initialize the filter.
	 */
	module.service("newsFilterService", function($location) {
		return {

			/**
			 * This function initializes the filter with the given parameters.
			 * @param scope object - where the filter should be initialized.
			 * @param presets object - key/value pairs with values which will be autofilled.
			 */
			init: function(scope, presets) {
				var filter = scope.filter || {
					data: {
						sortorder: 0,
						providerId: 0,
						categoryId: 0,
						sourceBees: !!scope.user, // only available if user is logged in
						sourceUser: !!scope.user, // only available if user is logged in
						amount: 20, // default
						offset: 0
					}
				};

				// prefill with query params
				ng.extend(filter.data, $location.search());

				// converts the timestamp into a date object
				var setDate = function(date) {
					if(filter.data[date]) {
						filter.data[date] = new Date(filter.data[date]);
					}
				};

				setDate("startPublishDate");
				setDate("endPublishDate");

				// set presets
				filter.presets = presets || {};
				ng.extend(filter.data, filter.presets); // set presets

				// put filter data into the scope.
				scope.filter = filter;
			}
		};
	});

})(angular, jQuery);
