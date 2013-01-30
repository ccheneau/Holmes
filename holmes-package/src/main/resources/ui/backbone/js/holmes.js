yepnope({
	load : {
		// css
		holmesCss : '/backbone/css/holmes.css',

		// lib
		jquery : '/backbone/js/lib/jquery-1.9.0.min.js',
		jqueryI18n : '/backbone/js/lib/jquery.i18n.properties-min-1.0.9.js',
		underscore : '/backbone/js/lib/underscore-min.js',
		backbone : '/backbone/js/lib/backbone.js',
		mustache : '/backbone/js/lib/mustache.js',

		// application
		application : '/backbone/js/application.js',

		// models
		videoFolder : '/backbone/js/models/videoFolder.js',
		audioFolder : '/backbone/js/models/audioFolder.js',
		pictureFolder : '/backbone/js/models/pictureFolder.js',
		podcast : '/backbone/js/models/podcast.js',

		// view controllers
		defaultView : '/backbone/js/views/defaultView.js',
		videoFolderListView : '/backbone/js/views/videoFolderListView.js',
		audioFolderListView : '/backbone/js/views/audioFolderListView.js',
		pictureFolderListView : '/backbone/js/views/pictureFolderListView.js',
		podcastListView : '/backbone/js/views/podcastListView.js',

		// admin view controllers
		videoFolderAdminView : '/backbone/js/views/admin/videoFolderAdminView.js',
		audioFolderAdminView : '/backbone/js/views/admin/audioFolderAdminView.js',
		pictureFolderAdminView : '/backbone/js/views/admin/pictureFolderAdminView.js',
		podcastAdminView : '/backbone/js/views/admin/podcastAdminView.js'

	},
	callback : {
		"jqueryI18n" : function() {
			// Initialize i18n
			$.i18n.properties({
				name : 'messages',
				path : '/backbone/bundle/',
				mode : 'map',
				callback : function() {
					// Internationalize i18n elements
					$(".i18n").each(function(i, elem) {
						if ($(elem).data('msg') != undefined)
							$(elem).html(($.i18n.prop($(elem).data('msg'))));
					});
				}
			});
			console.log("i18n loaded ...");
		}
	},
	complete : function() {
		console.log('Launching application ...');

		window.defaultView = new Application.Views.DefaultView();

		window.videoFolders = new Application.Collections.VideoFolders();
		window.videoFolderListView = new Application.Views.VideoFolderListView({
			collection : videoFolders
		});
		window.videoFolderAdminView = new Application.Views.VideoFolderAdminView({
			collection : videoFolders
		});

		window.audioFolders = new Application.Collections.AudioFolders();
		window.audioFolderListView = new Application.Views.AudioFolderListView({
			collection : audioFolders
		});
		window.audioFolderAdminView = new Application.Views.AudioFolderAdminView({
			collection : audioFolders
		});

		window.pictureFolders = new Application.Collections.PictureFolders();
		window.pictureFolderListView = new Application.Views.PictureFolderListView({
			collection : pictureFolders
		});
		window.pictureFolderAdminView = new Application.Views.PictureFolderAdminView({
			collection : pictureFolders
		});

		window.podcasts = new Application.Collections.Podcasts();
		window.podcastListView = new Application.Views.PodcastListView({
			collection : podcasts
		});
		window.podcastAdminView = new Application.Views.PodcastAdminView({
			collection : podcasts
		});

		window.router = new Application.Router.RoutesManager({
			videoFolders : videoFolders,
			videoFolderAdminView : videoFolderAdminView,
			audioFolders : audioFolders,
			audioFolderAdminView : audioFolderAdminView,
			pictureFolders : pictureFolders,
			pictureFolderAdminView : pictureFolderAdminView,
			podcasts : podcasts,
			podcastAdminView : podcastAdminView,
			defaultView : defaultView
		});

		Backbone.history.start();
		console.log('Application launched');
	}
});