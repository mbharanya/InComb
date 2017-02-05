/**
 * This module contains services/controllers/... for the languages of a user.
 */
(function(ng, $) {
	var module = ng.module("userLocales", [
		"resources"
	]);

	/**
	 * This controller loads all available locales from the server and puts these into the scope with the flag if the user
	 * has enabled the locale. It provides functions to add or remove a user locale.
	 */
	module.controller("userLocalesCtrl", function(localesResource, userLocalesResource, userLocaleResource, $scope) {

		// load all avaiable locales
		localesResource.get().$promise.then(function(data) {
			$scope.locales = {};

			// put all to the scope with false.
			ng.forEach(data, function(locale) {
				$scope.locales[locale] = false;
			});

			// load all user activated locales
			userLocalesResource.get({
				userId: $scope.user.id
			}).$promise.then(function(data) {
				// set all user activated locales to true.
				ng.forEach(data, function(locale) {
					$scope.locales[locale] = true;
				});
			});
		});

		/**
		 * Adds a new user locale to the server.
		 * @param locale object - the user locale to add.
		 */
		$scope.addLocale = function(locale) {
			userLocalesResource.post({
				userId: $scope.user.id,
				locale: locale
			});
		};

		/**
		 * Removes an existing user locale from the server.
		 * @param locale object - the user locale to remove.
		 */
		$scope.removeLocale = function(locale) {
			userLocaleResource.delete({
				userId: $scope.user.id,
				locale: locale
			});
		};
	});

})(angular, jQuery);
