yepnope({
	load: {
			jquery : '/backbone/js/3rd/jquery-1.8.2.min.js',
			underscore : '/backbone/js/3rd/underscore-min.js',
			backbone : '/backbone/js/3rd/backbone-min.js',
			mustache : '/backbone/js/3rd/mustache.js',
			
			//NameSpace
			application : '/backbone/js/application.js',

			//Models
			videoFolders : '/backbone/js/models/videoFolders.js',
			audioFolders : '/backbone/js/models/audioFolders.js',
			pictureFolders : '/backbone/js/models/pictureFolders.js',
			podcasts : '/backbone/js/models/podcasts.js',
			
			//Controllers
			videoFoldersView : '/backbone/js/views/videoFoldersView.js',
			audioFoldersView : '/backbone/js/views/audioFoldersView.js',
			pictureFoldersView : '/backbone/js/views/pictureFoldersView.js',
			podcastView : '/backbone/js/views/podcastsView.js',
			
			//Routes
			routes : '/backbone/js/routes.js'			
		},
	callback : {
		"routes" : function () {
			console.log("routes loaded ...");
			}
		},
	complete : function () {
			console.log('Lauching application ...');
			window.videoFolders = new Application.Collections.VideoFolders();
			window.videoFoldersView = new Application.Views.VideoFoldersView({collection : videoFolders});
			
			window.audioFolders = new Application.Collections.AudioFolders();
			window.audioFoldersView = new Application.Views.AudioFoldersView({collection : audioFolders});
			
			window.pictureFolders = new Application.Collections.PictureFolders();
			window.pictureFoldersView = new Application.Views.PictureFoldersView({collection : pictureFolders});

			window.podcasts = new Application.Collections.Podcasts();
			window.podcastsView = new Application.Views.PodcastsView({collection : podcasts});

			window.router = new Application.Router.RoutesManager({
				videoFolders:videoFolders,
				audioFolders:audioFolders,
				pictureFolders:pictureFolders,
				podcasts:podcasts
			});
			
			Backbone.history.start();
		}
});