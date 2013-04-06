var Application = (function(application) {
	application.Models.Podcast = Backbone.Model.extend({
		urlRoot : "/backend/podcasts"
	});

	application.Collections.Podcasts = Backbone.Collection.extend({
		model : application.Models.Podcast,
		url : "/backend/podcasts"
	});
	return application;
}(Application));