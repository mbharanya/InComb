/**
 * This module contains services/controllers/... for the user settings.
 */
(function(ng, $) {
	var module = ng.module("userSettings", [
		"resources"
	]);

	/**
	 * This controller puts the user data into the scope which can be used for an update form. It provides
	 * an update function which saves the changed data on the server.
	 */
	module.controller("userSettingsCtrl", function(userResource, userInitializer, $timeout, $rootScope, $scope) {

		// user data which can be used for an update form.
		$scope.updateUser = ng.copy($rootScope.user);

		/**
		 * Saves the scope.updateUser user data on the server.
		 * If successful then a success message will be set into the scope, otherwise the error messages
		 * will be set.
		 */
		$scope.update = function() {
			var userData = ng.copy($scope.updateUser);
			delete userData.passwordRetype;

			userResource.put({ userId: $scope.user.id }, userData).$promise.then(
				function(data) {

					// update the data of the current logged in user with the changed data.
					userInitializer($scope.updateUser);

					// set success and remove old error messages.
					$scope.success = data.success;
					$scope.successMsg = data.message;
					delete $scope.errors;

					// hide success message after 10s
					$timeout(function() {
						delete $scope.success;
						delete $scope.successMsg;
					}, 10000);
				},
				function(response) {
					// if an error occurred then put the error messages into the scope.

					$scope.success = response.data.success;
					delete $scope.successMsg;
					$scope.errors = response.data;
				}
			);
		};

		/* Not fully implemented.
		$scope.delete = function(){
			if (confirm($translate("actions.deleteUserConfirm").then(function(text){return text;}))){
				userResource.delete({ userId: $scope.user.id }.$promise.then(
					function(data){
						$scope.success = data.success;
						delete $scope.errors;
					},
					function(data){
						$scope.success = data.success;
						$scope.errors = data;
					}
				));
			}
		};*/
	});

})(angular, jQuery);
