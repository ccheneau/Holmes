var Application = (function(application) {
	application.Models.PictureFolder = Backbone.Model.extend({
		urlRoot : "/backend/backbone/pictureFolders"
	});

	application.Collections.PictureFolders = Backbone.Collection.extend({
		model : application.Models.PictureFolder,
		url : "/backend/backbone/pictureFolders"
	});
	return application;
}(Application));