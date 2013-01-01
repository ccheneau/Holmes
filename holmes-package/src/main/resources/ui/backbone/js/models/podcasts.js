var Application = (function (application) {
	application.Models.Podcasts = Backbone.Model.extend({
		urlRoot :"/backend/backbone/podcasts"
	});
	
	application.Collections.Podcasts = Backbone.Collection.extend({
		model : application.Models.Podcasts,
		all : function () {
			this.url = "/backend/backbone/podcasts";
			return this;
		},
	});
	return application;
}(Application));