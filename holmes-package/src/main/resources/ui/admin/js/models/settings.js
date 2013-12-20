var Application = (function(application) {
	application.Models.Settings = Backbone.Model.extend({
		urlRoot : "/backend/settings"
	});

	return application;
}(Application));