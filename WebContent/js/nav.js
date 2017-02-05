/**
 * This module contains services/controllers/... for the navigation.
 */
(function(ng, $) {
	var module = ng.module("nav", [
		"resources"
	]);

	/**
	 * This controller loads all categories from the server and puts these into the scope.
	 * They can be used to display the navigation.
	 */
	module.controller("navCtrl", function(categoriesResource, $scope) {

		// load categories
		categoriesResource.get({
			"moduleId": 1
		}).$promise.then(function(data) {
			$scope.categories = data.results;
		});
	});

})(angular, jQuery);
