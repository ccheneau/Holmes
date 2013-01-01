var Application = (function () {
	var application = {};
	application.Models = {};
	application.Collections = {};
	application.Views = {};
	application.Router = {};
	
	application.Router.RoutesManager = Backbone.Router.extend({
		initialize : function(args) {
			this.videoFolders = args.videoFolders;
			this.audioFolders = args.audioFolders;
			this.pictureFolders = args.pictureFolders;
			this.podcasts = args.podcasts;
			this.defaultView = args.defaultView;
		},
		
		routes : {
			"videoFolders" : "videoFolders",
			"audioFolders" : "audioFolders",
			"pictureFolders" : "pictureFolders",
			"podcasts" : "podcasts",
			"*path" : "root"
		},
		
		videoFolders : function () {
			this.videoFolders.all().fetch();
		},
		
		audioFolders : function () {
			this.audioFolders.all().fetch();
		},
		
		pictureFolders : function () {
			this.pictureFolders.all().fetch();
		},
		
		podcasts : function () {
			this.podcasts.all().fetch();
		},
		
		root : function () {
			this.defaultView.render();
		},
	});

	return application;
}());