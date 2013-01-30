var Application = (function(application) {
	application.Models.AudioFolder = Backbone.Model.extend({
		urlRoot : "/backend/backbone/audioFolders"
	});

	application.Collections.AudioFolders = Backbone.Collection.extend({
		model : application.Models.AudioFolder,
		all : function() {
			this.url = "/backend/backbone/audioFolders";
			return this;
		},
	});
	return application;
}(Application));