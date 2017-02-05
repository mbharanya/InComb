/**
 * This module contains services/controllers/... for the profile.
 */
(function(ng, $) {
	var module = ng.module("profile", [
		"router"
	]);

	// define url /{userName}
	module.config(function(routerProvider) {
		routerProvider.when("profile", "/:username", {
			controller: "profileCtrl",
			templateUrl: "/html/profile.html",
			resolve: {
				// wait till the current logged in user is loaded.
				user: function(getUser) {
					return getUser(false);
				}
			}
		});
	});

	/**
	 * This controller loads the user with the username in the URL and puts it into the scope. It sets some function to the user
	 * to interact with it (fly with, fly away, ...).
	 */
	module.controller("profileCtrl", function(usersResource, userInsResource, userCombsResource, userCommentsResource, $routeParams, pageTitle, router, $scope) {

		// initializes with scope.profileUser.
		var init = function() {

			// change page title
			pageTitle.change($scope.profileUser.displayName);

			/**
			 * Returns true if the current profile is of the current logged in user.
			 */
			$scope.isLoggedInUser = function() {
				return $scope.user && $scope.profileUser.id == $scope.user.id;
			};

			// if it's the current user or the profile is not private
			if(!$scope.profileUser.privateProfile || $scope.isLoggedInUser()) {

				/**
				 * Loads ins of the profile user and puts these into the scope.
				 * @param offset number - the start index of the ins.
				 * @param count number - the amount of ins after offset to load.
				 */
				$scope.loadIns = function (offset, count) {
					userInsResource.get({
						userId: $scope.profileUser.id,
						loggedInUserId: $scope.user ? $scope.user.id : 0,
						offset: offset,
						count: count
					}).$promise.then(function(response) {
						$scope.userIns = $scope.userIns || [];
						$scope.userIns.push.apply($scope.userIns, response.results);
						$scope.totalIns = response.totalHits;
						$scope.hasMoreIns = response.totalHits > offset + count;
						$scope.loadMoreIns = function() {
							if(!$scope.hasMoreIns) return;
							$scope.loadIns(offset + count, count);
						};
					});
				};

				/**
				 * Loads combs of the profile user and puts these into the scope.
				 * @param offset number - the start index of the combs.
				 * @param count number - the amount of combs after offset to load.
				 */
				$scope.loadCombs = function(offset, count) {
					userCombsResource.get({
						userId: $scope.profileUser.id,
						loggedInUserId: $scope.user ? $scope.user.id : 0,
						offset: offset,
						count: count
					}).$promise.then(function(response) {
						$scope.userCombs = $scope.userCombs || [];
						$scope.userCombs.push.apply($scope.userCombs, response.results);
						$scope.totalCombs = response.totalHits;
						$scope.hasMoreCombs = response.totalHits > offset + count;
						$scope.loadMoreCombs = function() {
							if(!$scope.hasMoreCombs) return;
							$scope.loadCombs(offset + count, count);
						};
					});
				};

				/**
				 * Loads comments of the profile user and puts these into the scope.
				 * @param offset number - the start index of the comments.
				 * @param count number - the amount of comments after offset to load.
				 */
				$scope.loadComments = function(offset, count) {
					userCommentsResource.get({
						userId: $scope.profileUser.id,
						offset: offset,
						count: count
					}).$promise.then(function(response) {
						$scope.userComments = $scope.userComments || [];
						$scope.userComments.push.apply($scope.userComments, response.results);
						$scope.totalComments = response.totalHits;
						$scope.hasMoreComments = response.totalHits > offset + count;
						$scope.loadMoreComments = function() {
							if(!$scope.hasMoreComments) return;
							$scope.loadComments(offset + count, count);
						};
					});
				};

				/**
				 * The logged in user starts flying with the given user.
				 * @param flyWithId number - the user to fly with.
				 */
				$scope.flyWith = function(flyWithId) {
					$scope.user.flyWith(flyWithId, function(data) {
						$scope.profileUser.withFlyings.splice(0, 0, $scope.user);
						$scope.$apply();
					});
				};

				/**
				 * The logged in user stops flying with the given user.
				 * @param flyWithId number - the user to fly away.
				 */
				$scope.flyAway = function(flyWithId) {
					$scope.user.flyAway(flyWithId, function() {
						for (var i = 0; i < $scope.profileUser.withFlyings.length; i ++) {
	                   		if ($scope.profileUser.withFlyings[i].id === $scope.user.id) {
	                           $scope.profileUser.withFlyings.splice(i, 1);
	                           $scope.$apply(); // apply changes in the scope.
	                           break;
	                        }
	                    }
					});
				};

				// inital load of ins, combs and comments
				$scope.loadIns(0, 10);
				$scope.loadCombs(0, 10);
				$scope.loadComments(0, 10);
			}
		};

		// if the logged in user is the requested profile user then use scope.user.
		if($scope.user && $routeParams.username == $scope.user.username) {
			$scope.profileUser = $scope.user;
			init();
		}
		else {
			// load the user from the server.
			usersResource.get({
				username: $routeParams.username,
				details: true
			}).$promise.then(function(data) {
				if(data.results.length) {
					$scope.profileUser = data.results[0];
					init();
	            } else {
		            // if the user wasn't found redirect to the not found page.
	                router.go("404");
	            }
			});
		}
	});


})(angular, jQuery);