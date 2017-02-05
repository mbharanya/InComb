/**
 * This is the main module of the app. It provides some useful services and configures the modules.
 */
(function(ng, $) {
	// dependcies to all other modules.
	var module = ng.module("incomb", [
		"pascalprecht.translate",
		"ngTagsInput",
		"infinite-scroll",
		"angularMoment",
		"errors",
		"home",
		"news",
		"category",
		"provider",
		"user",
		"settings",
		"router",
		"nav",
		"tagPreferences",
		"categoryPreferences",
		"userSettings",
		"search",
		"forms",
		"comb",
		"read",
		"userLocales",
		"slider",
		"styles",
		"profile" // has to be last because of the URLs.
	]);

	/**
	 * Sets the $locationProvider to HTML5 mode so we don't need to use the hash in the URL.
	 * Configures the $translateProvider that it automatically load the translation files from
	 * the right resource on the server.
	 */
	module.config(function($locationProvider, $translateProvider) {
		$locationProvider.html5Mode(true);

		$translateProvider.determinePreferredLanguage();
		$translateProvider.useStaticFilesLoader({
			prefix: '/api/translations/',
			suffix: ''
  		});
	});

	/**
	 * Sets the locale of the "moment" module to the current.
	 */
	module.run(function(amMoment, localeUtil) {
		amMoment.changeLocale(localeUtil.getCurrent());
	});

	/**
	 * This service is for modifing the window title. The <title> tag.
	 */
	module.service("pageTitle", function($rootScope, $translate) {
		return {

			/**
			 * Changes the window title to the given string and appends the general appendix to it.
			 * @param title string - the new title.
			 */
			change: function(title) {
				$translate("pageTitles.appendix").then(function(appendix) {
					$rootScope.pageTitle = title + appendix;
				});
			}
		}
	});

	/**
	 * This service is to work with locales.
	 */
	module.service("localeUtil", function($translate) {
		return {

			/**
			 * @return string - the current browser locale.
			 */
			getCurrent: function() {
				var locale = $translate.preferredLanguage();

				if(locale.indexOf("_") > -1) {
					var parts = locale.split("_");
					locale = parts[0] + "_" + parts[1].toUpperCase();
				}

				return locale;
			}
		};
	});

	/**
	 * This filter removes all tags from the given input.
	 */
	module.filter("plain", function() {
		return function(text) {
			return String(text).replace(/<[^>]+>/gm, '');
		};
	});

	/**
	 * This filter encodes the given URL part.
	 */
	module.filter("urlencode", function() {
		return function(text) {
			return encodeURIComponent(String(text));
		};
	});

	/**
	 * This directive executes the function in the after-render attribute after the element was rendered.
	 */
	module.directive("afterRender", function($timeout) {
		return function(scope, element, attrs) {
			$timeout(function() {
				scope.$eval(attrs.afterRender);
			});
		};
	});

})(angular, jQuery);
