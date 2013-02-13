(function ($) {
	$.fn.message = function(options) {
		this.html("<div class='alert alert-" + options.type + " alert-block fade in'><button class='close' data-dismiss='alert' type='button'>×</button><strong>" + options.text + "</strong></div>");
	};
})(jQuery);
