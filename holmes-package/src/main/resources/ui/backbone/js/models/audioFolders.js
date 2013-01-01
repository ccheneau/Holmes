var Application = (function (application) {
	application.Models.AudioFolders = Backbone.Model.extend({
		urlRoot :"/backend/backbone/audioFolders"
	});
	
	application.Collections.AudioFolders = Backbone.Collection.extend({
		model : application.Models.AudioFolders,
		all : function () {
			this.url = "/backend/backbone/audioFolders";
			return this;
		},
	});
	return application;
}(Application));