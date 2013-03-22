var Application = (function(application) {
	application.Models.Podcast = Backbone.Model.extend({
		urlRoot : "/backend/backbone/podcasts"
	});

	application.Collections.Podcasts = Backbone.Collection.extend({
		model : application.Models.Podcast,
		url : "/backend/backbone/podcasts"
	});
	return application;
}(Application));