var Application = (function (application) {
	application.Models.PictureFolders = Backbone.Model.extend({
		urlRoot :"/backend/backbone/pictureFolders"
	});
	
	application.Collections.PictureFolders = Backbone.Collection.extend({
		model : application.Models.PictureFolders,
		all : function () {
			this.url = "/backend/backbone/pictureFolders";
			return this;
		},
	});
	return application;
}(Application));