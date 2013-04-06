var Application = (function(application) {
	application.Models.AudioFolder = Backbone.Model.extend({
		urlRoot : "/backend/audioFolders"
	});

	application.Collections.AudioFolders = Backbone.Collection.extend({
		model : application.Models.AudioFolder,
		url : "/backend/audioFolders"
	});
	return application;
}(Application));