var Application = (function (application) {
	application.Models.VideoFolders = Backbone.Model.extend({
		urlRoot :"/backend/backbone/videoFolders"
	});
	
	application.Collections.VideoFolders = Backbone.Collection.extend({
		model : application.Models.VideoFolders,
		all : function () {
			this.url = "/backend/backbone/videoFolders";
			return this;
		},
	});
	return application;
}(Application));