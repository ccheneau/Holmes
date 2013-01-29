var Application = (function (application) {
	application.Models.PictureFolder = Backbone.Model.extend({
		urlRoot : "/backend/backbone/pictureFolders"
	});
	
	application.Collections.PictureFolders = Backbone.Collection.extend({
		model : application.Models.PictureFolder,
		all : function() {
			this.url = "/backend/backbone/pictureFolders";
			return this;
		},
	});
	return application;
}(Application));