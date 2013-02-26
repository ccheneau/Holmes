var Application = (function(application) {
	application.Models.Settings = Backbone.Model.extend({
		urlRoot : "/backend/backbone/settings"
	});

	return application;
}(Application));