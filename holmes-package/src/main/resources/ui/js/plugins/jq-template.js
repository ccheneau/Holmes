/**
 * JQuery plugin - get template
 */
(function ($) {
	$.getTemplate = function(template) {
		return $.ajax({
			type : "GET",
			url : "/templates/" + template,
			async : false,
			cache : true
		}).responseText;
	};
})(jQuery);