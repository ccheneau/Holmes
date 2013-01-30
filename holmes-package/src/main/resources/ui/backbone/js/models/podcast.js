var Application = (function(application) {
	application.Models.Podcast = Backbone.Model.extend({
		urlRoot : "/backend/backbone/podcasts"
	});

	application.Collections.Podcasts = Backbone.Collection.extend({
		model : application.Models.Podcast,
		all : function() {
			this.url = "/backend/backbone/podcasts";
			return this;
		},
	});
	return application;
}(Application));