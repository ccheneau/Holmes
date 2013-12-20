/**
 * JQuery plugin - get mustache template
 */
(function ($) {
	$.getTemplate = function(template) {
		return $.ajax({
			type : "GET",
			url : "/admin/templates/" + template,
			async : false
		}).responseText;
	};
})(jQuery);