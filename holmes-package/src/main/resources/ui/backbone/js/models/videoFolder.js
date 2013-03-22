var Application = (function(application) {
	application.Models.VideoFolder = Backbone.Model.extend({
		urlRoot : "/backend/backbone/videoFolders"
	});

	application.Collections.VideoFolders = Backbone.Collection.extend({
		model : application.Models.VideoFolder,
		url : "/backend/backbone/videoFolders"
	});
	return application;
}(Application));