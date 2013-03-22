/**
 * JQuery plugin - get template
 */
(function ($) {
	$.getTemplate = function(template) {
		return $.ajax({
			type : "GET",
			url : "/backbone/templates/" + template,
			async : false,
			cache : true
		}).responseText;
	};
})(jQuery);