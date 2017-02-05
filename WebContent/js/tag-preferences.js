/**
 * This module contains services/controllers/... for tag preferences.
 */
(function(ng, $) {
	var module = ng.module("tagPreferences", [
		"resources"
	]);

	/**
	 * This controller gets all tag preferences of the logged in user from the tagPreferencesResource
	 * and puts them into the scope. It provides an addTag and a deleteTag function to modify the
	 * tag preferences.
	 */
	module.controller("tagPreferencesCtrl", function(tagPreferencesResource, tagPreferenceResource, $scope) {

		// request all tag preferences
		tagPreferencesResource.get({
			userId: $scope.user.id
		}).$promise.then(function(data) {

			// put the tag preferences to the scope.
			$scope.tags = data;
		});

		/**
		 * Saves the given tag preference on the server.
		 * @param tag object - the tag preference to add.
		 */
		$scope.addTag = function(tag) {
			tag.userId = $scope.user.id;
			tagPreferencesResource.post({ userId: $scope.user.id }, tag);
		};

		/**
		 * Deletes the given tag preference on the server.
		 * @param tag object - the tag preference to delete.
		 */
		$scope.deleteTag = function(tag) {
			tagPreferenceResource.delete(tag);
		};
	});

})(angular, jQuery);
