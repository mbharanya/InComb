/**
 * This module contains services/controllers/... for news.
 */
(function(ng, $) {
	var module = ng.module("news", [
		"filter",
		"resources",
		"router",
		"ngSanitize"
	]);

	// define url /news/{newsId}
	module.config(function(routerProvider) {
		routerProvider.when("news", "/news/:id", {
			controller: "newsCtrl",
			templateUrl: "/html/news.html",
			resolve: {
				// wait till the current logged in user is loaded.
				user: function(getUser) {
					return getUser(false);
				}
			}
		});
	});

	/**
	 * This controller loads the news with the news id in the URL from the server and puts it into the scope.
	 */
	module.controller("newsCtrl", function(newsRecResource, newsLoader, $routeParams, $translate, pageTitle, router, $scope) {
		newsRecResource.get({
			id: $routeParams.id,
			userId: $scope.user ? $scope.user.id : 0
		}).$promise.then(function(data) {
			// prepare the loaded news.
			newsLoader.prepare(data, $scope);

			// put news into scope.
			$scope.news = data;

			// set new page title.
			pageTitle.change(data.title);
		}, function(response) {
			// if no news was found than redirect to the not found page.
			router.go("404");
		});
	});

	/**
	 * This service should be used to load news with the newsResource from the server and to prefill a news
	 * with useful functions.
	 */
	module.service("newsLoader", function(newsResource, combItemResource, commentsResource, votesResource, insResource, combsResource, localeUtil, $log, $location, router, $window, $sce) {
		var prepare = function(mainNews, scope, newsParams) {

			// put all news into an array.
			var allNews = [mainNews];
			allNews.push.apply(allNews, mainNews.otherNews);

			var initNews = function(news) {
				// trust so we can show the news as HTML.
				news.htmlText = $sce.trustAsHtml(news.text);

				/**
				 * The currently logged in user gives an In for the news.
				 * This will be saved to the server and the voteAmounts will be refreshed afterwards.
				 */
				news.in = function(){
					votesResource.post({
						contentId: news.contentId,
						userId: scope.user.id,
						up: 1
					}).$promise.then(function(response){
						news.voteAmounts.insAmount = response.model.insAmount;
						news.voteAmounts.combsAmount = response.model.combsAmount;
					},
					function(response){
						router.go("500");
					});
				};

				/**
				 * The currently logged in user gives a Comb for the news.
				 * This will be saved to the server and the voteAmounts will be refreshed afterwards.
				 */
				news.comb = function(){
					votesResource.post({
						contentId: news.contentId,
						userId: scope.user.id,
						up: 0
					}).$promise.then(function(response){
						news.voteAmounts.insAmount = response.model.insAmount;
						news.voteAmounts.combsAmount = response.model.combsAmount;
					},
					function(response){
						router.go("500");
					});
				};

				/**
				 * Puts the news to the comb of the currently logged in user.
				 */
				news.inMyComb = function() {
					combItemResource.post({
						userId: scope.user.id,
						contentId: news.id
					}).$promise.then(function(data) {
						news.combItem = data;
					});
				};

				/**
				 * Removes the news from the comb of the currently logged in user.
				 */
				news.outOfMyComb = function() {
					combItemResource.delete({
						userId: scope.user.id,
						contentId: news.id
					}).$promise.then(function(data) {
						news.combItem = null;
					});
				};

				/**
				 * Marks the comb item of the currently logged in user with the news as read.
				 */
				news.markRead = function() {
					if(news.combItem) {
						news.combItem.readDate = new Date().getTime();

						combItemResource.put({
							userId: scope.user.id,
							contentId: news.id
						}, news.combItem);
					}
				};

				// puts a new newComment to the news with empty comment value.
				var resetComment = function(){
					if (scope.user){
						news.newComment = {
							userId: scope.user.id,
							comment: '',
							contentId: news.id
						};
					}
				};

				resetComment(); // reset at start

				/**
				 * Submits the values in news.newComment to the server.
				 */
				news.addComment = function(){
					commentsResource.post(news.newComment).$promise.then(function(response){
						news.comments.push(response.model);
						resetComment();
					}, function(response){
						if(response.status === 400){
							scope.errors = scope.errors || { fields: {} };
							scope.errors.fields["comment-"+news.id] = response.comment;
						}else{
							router.go("500");
						}
					});
				};

				/**
				 * Collapses all expanded news of the group and opens this news.
				 */
				news.expand = function() {
					if(news.collapsed) {
						ng.forEach(allNews, function(news) {
							news.collapsed = true;
						});

						news.collapsed = false;
					}
				};

				if(newsParams) {
					// open in read view if newsParams were given.

					var params = ng.extend({
						selectedId: news.id
					}, newsParams);

					news.go = function() {
						router.go("read", {}, params, {
							inheritUrlParams: false,
							inheritQueryParams: false
						});
					};
				}
				else {
					// open the news in a new window/tab.
					news.go = function() {
						$window.open(news.link);
						news.markRead();
					};
				}
			};

			// prepare main news
			initNews(mainNews);

			// prepare each other news and collapse these.
			ng.forEach(mainNews.otherNews, function(otherNews) {
				otherNews.collapsed = true;
				initNews(otherNews);
			});
		};

		var load = function(params, scope, callback) {
			params = ng.copy(params); // copy object

			// set common filter params
			params.locale = localeUtil.getCurrent();
			params.userId = scope.user ? scope.user.id : 0;

			scope.news = scope.news || {
				records: []
			};

			// this is true when news are loading currently.
			scope.news.loading = true;

			/**
			 * Loads 10 more news with the same params.
			 */
			scope.news.loadMore = function() {
				if(scope.news.loading) return;

				params.offset = parseInt(params.offset, 10) + parseInt(params.amount, 10);
				load(params, scope, null); // execute callback only once -> null
			};

			// converts the date object to a timestamp
			var setDate = function(date) {
				if(params[date]) {
					var timestamp = parseInt(params[date], 10);
					if(!$window.isNaN(timestamp)) {
						params[date] = timestamp;
					}
					else {
						// we assume that it's a Date object
						params[date] = params[date].getTime();
					}
				}
			};

			setDate("startPublishDate");
			setDate("endPublishDate");

			// loads the news from the server
			newsResource.get(params).$promise.then(function(data) {

				// prepare each news
				ng.forEach(data.results, function(news) {
					prepare(news, scope, params);
				});

				// add the news to the others.
				scope.news.records.push.apply(scope.news.records, data.results);
				scope.news.totalHits = data.totalHits;
				scope.news.hasMore = (params.offset + params.amount) < data.totalHits;

				// loading done.
				scope.news.loading = false;

				// call callback if one was given.
				if(ng.isFunction(callback)) {
					callback(scope.news);
				}
			}, function() {
				// loading done.
				scope.news.loading = false;
			});
		};

		return {

			/**
			 * Loads the news for the given parameters from the server, prepares them and puts them into the scope.
			 * It puts functions into the scope to load more news and to check if there are more news for the criteria
			 * on the server.
			 *
			 * @param params object - the criteria which will be used as query paramaters.
			 * @param scope object - the scope in which the news should be put.
			 * @param callback function - the callback will be called after the news were loaded.
			 */
			load: load,

			/**
			 * Prefills the given news with useful functions to save a comemnt give an in or comb, ...
			 * @param mainNews object - the news which will be prepared.
			 * @param scope object - the scope containing the current logged in user.
			 * @param newsParams object - a criteria which matches with the mainNews and should be used
			 *			to open the news not in a new tab but in the read view with these params.
			 */
			prepare: prepare
		};
	});

})(angular, jQuery);
