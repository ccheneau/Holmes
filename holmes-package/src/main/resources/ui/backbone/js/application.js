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
			"addAudioFolder" : "addAudioFolder",
			"editAudioFolder/:id_folder" : "editAudioFolder", 
			"removeAudioFolder/:id_folder" : "removeAudioFolder",
			
			"pictureFolders" : "pictureFolders",
			"addPictureFolder" : "addPictureFolder",
			"editPictureFolder/:id_folder" : "editPictureFolder", 
			"removePictureFolder/:id_folder" : "removePictureFolder",
			
			"podcasts" : "podcasts",
			"addPodcast" : "addPodcast",
			"editPodcast/:id_folder" : "editPodcast", 
			"removePodcast/:id_folder" : "removePodcast",
			
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
			var videoFolder = new Application.Models.VideoFolder({id:id_folder});
			videoFolder.destroy({
					success : function(){
						this.videoFolders.all().fetch();
					}
				});
		},
		
		audioFolders : function() {
			this.audioFolders.all().fetch();
		},
		
		addAudioFolder : function() {
			this.audioFolderAdminView.render(new Application.Models.AudioFolder());
		},
		
		editAudioFolder : function(id_folder) {
			var audioFolder = new Application.Models.AudioFolder({id:id_folder});
			audioFolder.fetch({
					success : function(result){
						this.audioFolderAdminView.render(result);
					}
				});
		},
		
		removeAudioFolder : function(id_folder) {
			var audioFolder = new Application.Models.AudioFolder({id:id_folder});
			audioFolder.destroy({
					success : function(){
						this.audioFolders.all().fetch();
					}
				});
		},
		
		pictureFolders : function() {
			this.pictureFolders.all().fetch();
		},
		
		addPictureFolder : function() {
			this.pictureFolderAdminView.render(new Application.Models.PictureFolder());
		},
		
		editPictureFolder : function(id_folder) {
			var pictureFolder = new Application.Models.PictureFolder({id:id_folder});
			pictureFolder.fetch({
					success : function(result){
						this.pictureFolderAdminView.render(result);
					}
				});
		},
		
		removePictureFolder : function(id_folder) {
			var pictureFolder = new Application.Models.PictureFolder({id:id_folder});
			pictureFolder.destroy({
					success : function(){
						this.pictureFolders.all().fetch();
					}
				});
		},

		podcasts : function () {
			this.podcasts.all().fetch();
		},
		
		addPodcast : function() {
			this.podcastAdminView.render(new Application.Models.Podcast());
		},
		
		editPodcast : function(id_folder) {
			var podcast = new Application.Models.Podcast({id:id_folder});
			podcast.fetch({
					success : function(result){
						this.podcastAdminView.render(result);
					}
				});
		},
		
		removePodcast : function(id_folder) {
			var podcast = new Application.Models.Podcast({id:id_folder});
			podcast.destroy({
					success : function(){
						this.podcasts.all().fetch();
					}
				});
		},

		root : function() {
			this.defaultView.render();
		},
	});

	return application;
}());