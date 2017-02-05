/**
 * This module handles the currently logged in user.
 */
(function(ng, $) {
	var module = ng.module("user", [
		"router",
		"resources"
	]);

	// define url /register and /login
	module.config(function(routerProvider) {
		routerProvider.when("login", "/login", {
			controller: "loginCtrl",
			templateUrl: "/html/login.html"
		});

		routerProvider.when("register", "/register", {
			controller: "registerCtrl",
			templateUrl: "/html/register.html"
		});

		routerProvider.when("logout", "/logout", {
			controller: "logoutCtrl",
			templateUrl: "/html/login.html"
		});
	});

	/**
	 * This provider returns a function which can be used to get the current logged in user.
	 * The function returns a promise which will be resolved when the user was loaded.
	 * If rejectIfNotLoggedIn was set to true then the promise will be rejected if no user
	 * is logged in and the user will redirected to the login page.
	 *
	 * If the user is already in the scope then the user in the scope will be used.
	 *
	 * The user can be used to block controllers which requires the user.
	 */
	module.provider("getUser", function() {
		var userChecked = false;

		this.$get = function($rootScope, loggedInUserResource, userInitializer, $q, router) {
			return function(rejectIfNotLoggedIn) {
				return $q(function(resolve, reject) {

					// handles the promise when no user is logged in.
					var handleNotLoggedIn = function() {
						if(rejectIfNotLoggedIn) {

							// reject and redirect to login
							reject("User is not logged in.");
							router.go("login");
						}
						else {
							resolve(null);
						}
					};

					if(userChecked) {
						// if the user was already checked

						if($rootScope.user) {
							resolve($rootScope.user);
						}
						else {
							handleNotLoggedIn();
						}

						return;
					}

					// load the logged in user from the server.
					loggedInUserResource.get().$promise.then(function(data) {
						userInitializer(data);
						resolve($rootScope.user);
						userChecked = true;
					}, function() {
						userInitializer(null);
						handleNotLoggedIn();
						userChecked = true;
					});
				});
			};
		};
	});

	/**
	 * Puts the given user in the scope and sets functions on it to fly with, fly away and check if it is flying with a user.
	 * Every change (logouts too) has to be made with this service.
	 */
	module.service("userInitializer", function($rootScope, $injector) {
		return function(user) {
			if(user) {
				if($rootScope.user) {
					// new user data -> extend with old user
					ng.extend($rootScope.user, user);
				}
				else {
					// login -> add to scope.
					$rootScope.user = user;
				}
			}
			else {
				// logout -> remove from scope.
				delete $rootScope.user;
			}

			if($rootScope.user) {
	            // remove properties which aren't in the model
				delete $rootScope.user.$promise;
				delete $rootScope.user.$resolved;

				// load flyWithResource asynchronously to avoid circular dependencies
				var flyWithResource = $injector.get("flyWithResource");

				/**
				 * The logged in user starts flying with the given user.
				 * @param flyWithId number - the user to fly with.
				 * @param callback function - which will be called after the API call was successful.
				 */
	            $rootScope.user.flyWith = function(flyWithId, callback) {
	                flyWithResource.post({
	                    userId: $rootScope.user.id,
	                    flyWithId: flyWithId
	                }).$promise.then(function(data) {

		                // update model
	                    $rootScope.user.flyWiths.push(data);

	                    // call callback if existing
	                    if(ng.isFunction(callback)) {
		                    callback(data);
	                    }
	                });
	            };

				/**
				 * The logged in user stops flying with the given user.
				 * @param flyWithId number - the user to fly away.
				 * @param callback function - which will be called after the API call was successful.
				 */
	            $rootScope.user.flyAway = function(flyWithId, callback) {
	                flyWithResource.delete({
	                    userId: $rootScope.user.id,
	                    flyWithId: flyWithId
	                }).$promise.then(function() {

		                // update model
	                    for (var i = 0; i < $rootScope.user.flyWiths.length; i ++) {
	                   		if ($rootScope.user.flyWiths[i].id === flyWithId) {
	                           $rootScope.user.flyWiths.splice(i, 1);
	                           break;
	                        }
	                    }

						// call callback if existing
	                    if(ng.isFunction(callback)) {
		                    callback();
	                    }
	                });
	            };

				/**
				 * Checks if the logged in user is flying with the given user
				 * @param flyWithId number - the user to check.
				 * @return boolean - true if the logged in user is flying with the given user.
				 */
	            $rootScope.user.isFlyingWith = function(flyWithId) {
	                for (var i = 0; i < $rootScope.user.flyWiths.length; i ++) {
	                    if ($rootScope.user.flyWiths[i].id === flyWithId) {
	                        return true;
	                    }
	                }

	                return false;
	            };
	        }
		};
	});

	/**
	 * This controller provides the model for the login data and a login function which submits the data to the server.
	 * If the login was successful then it will be initialized with the userInitializer and redirected to the home view
	 * or the view which was set in the query params. If the login was unsuccessful then an error message will be added
	 * to the scope.
	 */
	module.controller("loginCtrl", function(loggedInUserResource, router, $location, $translate, pageTitle, userInitializer, $scope) {

		// change page title
		$translate("pageTitles.login").then(function(title) {
			pageTitle.change(title);
		});

		// model for login data
		$scope.data = {
			username: "",
			password: ""
		};

		// clear all error when the login data changes.
		$scope.$watchCollection("data", function() {
			$scope.errors = [];
		});

		/**
		 * Submits the login model to the server.
		 */
		$scope.login = function() {
			$scope.errors = [];

			// submit the login data.
			loggedInUserResource.post($scope.data).$promise.then(function(data) {

				// initialize the user model.
				userInitializer(data.model);

				// check if there is return to url param and redirect to it if it exists.
				var queryParams = $location.search();
				if(queryParams.returnTo) {
					$location.url(queryParams.returnTo);
				}
				else {
					// redirect to home view
					router.go("home");
				}
			}, function(data) {
				if(data.status == 401) {
					// put error message into the scope.
					$translate("errors.login.wrongCredentials").then(function(error) {
						$scope.errors.push(error);
					});
				}
			});
		};
	});

	/**
	 * Provides a model for the register data. The provided register function can be used to submit this data to the server.
	 * If the user was successfully registered then it will be put into the scope with the userInitializer and redirected
	 * to the home view. If the registration was unsuccessful then the errors will be added to the scope.
	 */
	module.controller("registerCtrl", function(usersResource, router, $translate, pageTitle, localeUtil, userInitializer, $scope) {

		// change page title.
		$translate("pageTitles.register").then(function(title) {
			pageTitle.change(title);
		});

		// initialize model for registration data
		$scope.data = {
			email: "",
			username: "",
			displayName: "",
			password: "",
			passwordRetype: "", // will not be sent to the server
			locale: localeUtil.getCurrent()
		};

		/**
		 * Registers the user with a server call.
		 */
		$scope.register = function() {
			var userData = ng.copy($scope.data);

			// not in the model of the server -> remove it.
			delete userData.passwordRetype;

			// call REST resource.
			usersResource.post(userData).$promise.then(function(data) {
				userInitializer(data.model);

				delete $scope.errors; // remove old errors

				// after registration -> go to home.
				router.go("home");
			},
            function(response) {
	            // add error messages to the scope.
                $scope.success = response.data.success;
                delete $scope.successMsg;
                $scope.errors = response.data;
            });
		};
	});

	/**
	 * This controller does a logout on the server and with the userInitializer and redirects
	 * the user to the login view.
	 */
	module.controller("logoutCtrl", function(loggedInUserResource, router, userInitializer) {
		// call REST resource.
		loggedInUserResource.delete().$promise.then(function() {
			userInitializer(null); // remove

			// after logout -> go to login.
			router.go("login");
		});
	});

	// register authentication check interceptor
	module.config(function($httpProvider) {
		$httpProvider.interceptors.push("authCheckInterceptor");
	});

	/**
	 * Checks every response error if the response status code is 401. In this case the
	 * user will be redirected to the login view.
	 * The 401 code means that no user is logged in but one is required.
	 */
	module.service("authCheckInterceptor", function($q, $log, $location, userInitializer) {
		return {
	        responseError: function(rejection) {
		        // check if response code is 401 and we're not already on the login view.
	            if (rejection.status === 401 && $location.path() != "/login") {
	                $log.info("Response code was 401.", rejection);

	                userInitializer(null); // logout user.

	                // we can't use our router because of problems with DI.
	                $location.path("/login").search("returnTo", $location.path());
	            }

	            return $q.reject(rejection);
	        }
		};
	});

})(angular, jQuery);
