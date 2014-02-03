yepnope({
	load : {
		// lib
		jquery : '/js/jquery-2.1.0.min.js',
		bootstrap : '/js/bootstrap.min.js',
		angular : '/js/angular.min.js',
		jqueryI18n : '/js/jquery.i18n.properties-min-1.0.9.js',

		// plugins
		logger : '/js/plugins/jq-logger.js',

		// application
		application : '/play/js/application.js'

	},
	callback : {
		"jquery" : function() {
		    $.ajaxSetup({ cache: false });
		},
		"jqueryI18n" : function() {
			// initialize i18n
			$.i18n.properties({
				name : 'messages',
				path : '/bundle/',
				mode : 'map',
				callback : function() {
					// Internationalize i18n elements
					$(".i18n").each(function(i, elem) {
						if ($(elem).data('msg') != undefined)
							$(elem).html(($.i18n.prop($(elem).data('msg'))));
					});
				}
			});
		}
	}
});