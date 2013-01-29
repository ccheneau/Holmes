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
			this.videoFolderAdminView = args.videoFolderAdminView;
			this.audioFolderAdminView = args.audioFolderAdminView;
			this.pictureFolderAdminView = args.pictureFolderAdminView;
			this.podcastAdminView = args.podcastAdminView;
		},
		
		routes : {
			"videoFolders" : "videoFolders",
			"addVideoFolder" : "addVideoFolder",
			"editVideoFolder/:id_folder" : "editVideoFolder", 
			"removeVideoFolder/:id_folder" : "removeVideoFolder", 
			"audioFolders" : "audioFolders",
			"pictureFolders" : "pictureFolders",
			"podcasts" : "podcasts",
			"*path" : "root"
		},
		
		videoFolders : function() {
			this.videoFolders.all().fetch();
		},
		
		addVideoFolder : function() {
			this.videoFolderAdminView.render(new Application.Models.VideoFolder());
		},
		
		editVideoFolder : function(id_folder) {
			var videoFolder = new Application.Models.VideoFolder({id:id_folder});
			videoFolder.fetch({
					success : function(result){
						this.videoFolderAdminView.render(result);
					}
				});
		},
		
		removeVideoFolder : function(id_folder) {
			if (confirm($.i18n.prop("msg.video.remove.confirm"))) {
				var videoFolder = new Application.Models.VideoFolder({id:id_folder});
				videoFolder.destroy({
						success : function(){
							this.videoFolders.all().fetch();
						}
					});
			}
		},
		
		audioFolders : function() {
			this.audioFolders.all().fetch();
		},
		
		pictureFolders : function() {
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