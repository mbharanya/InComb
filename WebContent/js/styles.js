/**
 * This module contains directives/services/... for stylings. No functionality.
 */
(function(ng, $) {
	var module = ng.module("styles", []);

	/**
	 * This directive makes fixes an element when the user scrolls and the element is going to hide.
	 * If the window height is smaller than the element height than it won't be fixed.
	 * The class in the attribute "fixed" will be set to the element if it will be fixed.
	 */
	module.directive("fixed", function($window) {
		return {
			link: function(scope, element, attrs) {
				element = $(element);
				$window = $($window);

				var check = function() {
					// check if the window is tall enough to show the whole lement.
					if(element.height() <= $window.height()) {
						element.toggleClass(attrs.fixed, $window.scrollTop() > element.parent().offset().top);
					}
					else {
						element.removeClass(attrs.fixed);
					}
				};

				// register on scroll and resize events of the window.
				$window.on("scroll resize", check);
			}
		};
	});
})(angular, jQuery);
