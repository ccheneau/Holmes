var Application = (function(application) {
	application.Models.PictureFolder = Backbone.Model.extend({
		urlRoot : "/backend/pictureFolders"
	});

	application.Collections.PictureFolders = Backbone.Collection.extend({
		model : application.Models.PictureFolder,
		url : "/backend/pictureFolders"
	});
	return application;
}(Application));