var Application = (function(application) {
	application.Models.VideoFolder = Backbone.Model.extend({
		urlRoot : "/backend/backbone/videoFolders"
	});

	application.Collections.VideoFolders = Backbone.Collection.extend({
		model : application.Models.VideoFolder,
		all : function() {
			this.url = "/backend/backbone/videoFolders";
			return this;
		}
	});
	return application;
}(Application));