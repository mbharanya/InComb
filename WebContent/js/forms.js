/**
 * This module contains services/controllers/... for forms.
 */
(function(ng, $) {
	var module = ng.module("forms", [
	]);

	/**
	 * This directive can be used to show an error message of a field referenced in the validate-error attribute.
	 * If no error exists than the element will hide. The error has to be in scope.errors.
	 */
	module.directive("validateError", function($translate) {
		return {
			link: function(scope, element, attrs) {
				var element = $(element);

				// removes the text and hides the element.
				var hideError = function() {
					element.hide();
					element.text("");
				};

				// watch for a change in scope.errors.
				scope.$watchCollection("errors", function() {
					if(ng.isObject(scope.errors) && scope.errors.fields) {
						var field = scope.errors.fields[attrs.validateError];
						if(field && !field.valid) {
							// show the localized error text for this field.
							$translate(field.message).then(function(msg) {
								element.text(msg);
								element.show();
							});
						}
						else {
							// no (more) error for this field.
							hideError();
						}
					}
				});

				// if a for attribute exists check for changes of the referenced field.
				if(attrs.for) {
					var input = $("#" + attrs.for);
					var ngModel = input.attr("ng-model");

					if(ngModel) {
						scope.$watch(ngModel, function() {
							hideError();
						});
					}
				}
			}
		}
	});

	/**
	 * This validator checks if the model value of this field is the same as in the referenced field.
	 * Can be used for a password retype field.
	 */
	module.directive("validateRetype", function() {
		return {
			restrict: 'A',
			require: '?ngModel',
			link: function(scope, element, attrs, ctrl) {
				// check for changes of this value
				scope.$watch(attrs.ngModel, function() {
					validate();
			 	});

			 	// check for changes of the other value
				scope.$watch(attrs.validateRetype, function (val) {
        			validate();
      			});

	  			// check if they are equal.
				var validate = function() {
					var val1 = ctrl.$viewValue;
					var val2 = scope.$eval(attrs.validateRetype);
					ctrl.$setValidity('validateRetype', ! val1 || ! val2 || val1 === val2);
				};
			}
		}
	});

})(angular, jQuery);
