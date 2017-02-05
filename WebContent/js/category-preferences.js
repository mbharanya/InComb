/**
 * This module contains services/controllers/... for category preferences.
 */
(function(ng, $) {
	var module = ng.module("categoryPreferences", [
		"resources"
	]);

	/**
	 * This controller gets all category preferences of the currently logged in user from the categoryPreferencesResource
	 * and puts them into the scope. It provides to every category preference a save function, which can be used to save
	 * a changed factor.
	 */
	module.controller("categoryPreferencesCtrl", function(categoryPreferencesResource, categoryPreferenceResource, $scope) {

		// get the category preferences from the server.
		categoryPreferencesResource.get({
			userId: $scope.user.id
		}).$promise.then(function(data) {
			$(data).each(function() {
				var preference = this;
				var timeout = null;

				/**
				 * This saves the category preference on the server if it won't be called again in 100ms after the call.
				 */
				this.save = function() {
					if(timeout) {
						clearTimeout(timeout);
					}

					timeout = setTimeout(function() {
						categoryPreferenceResource.put({
							userId: $scope.user.id,
							categoryId: preference.category.id
						}, preference);
					}, 100);
				};
			});

			// put the category preferences into the scope.
			$scope.categoryPreferences = data;
		});
	});

})(angular, jQuery);
