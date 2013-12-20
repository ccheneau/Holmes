var Application = (function(application) {
	application.Models.VideoFolder = Backbone.Model.extend({
		urlRoot : "/backend/videoFolders"
	});

	application.Collections.VideoFolders = Backbone.Collection.extend({
		model : application.Models.VideoFolder,
		url : "/backend/videoFolders"
	});
	return application;
}(Application));