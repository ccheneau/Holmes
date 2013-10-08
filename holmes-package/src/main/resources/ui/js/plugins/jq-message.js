/**
 * JQuery plugin - show message
 * option:
 * 		- type : success|info|warning|danger
 * 		- text : message text
 */
(function ($) {
	$.fn.message = function(options) {
		this.html("<div class='alert alert-" + options.type + " alert-block fade in'><button class='close' data-dismiss='alert' type='button'>×</button><strong>" + options.text + "</strong></div>");
	};
})(jQuery);
