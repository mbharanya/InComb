/**
 * This module contains services/controllers/... for the read view.
 */
(function(ng, $) {
	var module = ng.module("read", [
		"router",
		"ngSanitize"
	]);

	// define url /read
	module.config(function(routerProvider) {
		routerProvider.when("read", "/read", {
			controller: "readCtrl",
			templateUrl: "/html/read.html",
			resolve: {
				// wait till the current logged in user is loaded.
				user: function(getUser) {
					return getUser(false);
				}
			}
		});
	});

	/**
	 * The controller loads the news for the read view from the newsLoader with the params from the query string.
	 */
	module.controller("readCtrl", function(newsLoader, $translate, pageTitle, $location, $sce, $window, $scope) {

		// get params to load news from url.
		var newsParams = $location.search();

		// load the news with the params.
		newsLoader.load(newsParams, $scope, function(news) {

			// put all found news in this array
			var allNews = [];
			ng.forEach(news.records, function(news) {
				allNews.push(news);
				allNews.push.apply(allNews, news.otherNews);
			});

			ng.forEach(allNews, function(news) {

				// if the user clicks on "read more" then open it in the read frame.
				news.go = function() {
					news.selectNews();
				};

				// function to select a news and read in the frame.
				news.selectNews = function() {
					$scope.selectedNews = news;
					$scope.selectedNews.frameLink = $sce.trustAsResourceUrl( $scope.selectedNews.link );
					news.markRead();
				};

				// check if this news is the requested one
				if(news.id == newsParams.selectedId) {
					news.selectNews();
				}
			});
		});

		// back function to go back in browser history.
		$scope.back = function() {
			$window.history.back();
		};

		// set page title
		$translate("pageTitles.read").then(function(title) {
			pageTitle.change(title);
		});
	});

})(angular, jQuery);
