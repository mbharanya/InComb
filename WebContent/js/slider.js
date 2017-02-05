/**
 * This module provides a directive for a slider.
 */
(function(ng, $) {
	var module = ng.module("slider", []);

	/**
	 * This directive has to be set to a wrapper over all items and navigation elements.
	 * The navigation elements (forward, backward) must have the attribute slide containing the direction (left or right).
	 * The navigation elements will have the class "visible" if they can be used.
	 *
	 * The wrapper must have set overflow: hidden; because the slider will use margin-left to slide in the wrapper.
	 * CSS transitions are supported.
	 */
	module.directive("slider", function() {
		return {
			scope: true, // new scope for this element
			link: function(scope, element, attrs) {
				var slider = $(element);
				var buttons = slider.find("[slide]");
				var content = slider.find(attrs.slider);

				var isSliding = false; // true when the items are sliding (css transitions).

				// checks if all navigation elements can be used and sets the visible class.
				var checkVisible = function() {
					buttons.each(function() {
						var button = $(this);
						var isLeft = button.attr("slide") == "left";

						var left = -parseInt(content.css("margin-left"));

						// if we aren't completly left or right in the wrapper.
						var visible = isLeft ? left > 0 : left < (content.outerWidth()-parseInt(attrs.slideAmount));
						button.toggleClass("visible", visible);

						// load more.
						if(!isLeft && !visible && attrs.loadMore) {
							scope.$eval(attrs.loadMore);
						}
					});
				};

				// for each navigation element.
				buttons.each(function() {
					var button = $(this);
					var isLeft = button.attr("slide") == "left";

					// if the user clicks on the navigation element -> slide!
					button.click(function(event) {
						event.preventDefault();

						// stop if the last slide wasn't finished or the button can't be used.
						if(isSliding || !button.hasClass("visible")) return;

						isSliding = true; // start sliding

						var left = parseInt(content.css("margin-left")); // old slided value.
						if(isLeft) {
							content.css({
								"margin-left": left + parseInt(attrs.slideAmount) // add slide amount to the old value.
							});
						}
						else {
							content.css({
								"margin-left": left - parseInt(attrs.slideAmount)// subtract slide amount to the old value.
							});
						}

						// wait until the transition has ended.
						content.one("transitionend webkitTransitionEnd mozTransitionEnd", function() {
							checkVisible(); // check new visibility of the navigation elements.
							isSliding = false; // finished sliding
						});
					});
				});

				// check navigation elements visibility when the slider was completly loaded.
				scope.initSlider = checkVisible;
			}
		};
	});
})(angular, jQuery);
