yepnope({
	load: {
			// css
			holmesCss : '/backbone/css/holmes.css',
			
			// lib
			jquery : '/backbone/js/lib/jquery-1.8.3.min.js',
			jqueryI18n : '/backbone/js/lib/jquery.i18n.properties-min-1.0.9.js',
			underscore : '/backbone/js/lib/underscore-min.js',
			backbone : '/backbone/js/lib/backbone-min.js',
			mustache : '/backbone/js/lib/mustache.js',
			
			// application
			application : '/backbone/js/application.js',

			// models
			videoFolders : '/backbone/js/models/videoFolders.js',
			audioFolders : '/backbone/js/models/audioFolders.js',
			pictureFolders : '/backbone/js/models/pictureFolders.js',
			podcasts : '/backbone/js/models/podcasts.js',
			
			// view controllers
			defaultView : '/backbone/js/views/defaultView.js',
			videoFoldersView : '/backbone/js/views/videoFoldersView.js',
			audioFoldersView : '/backbone/js/views/audioFoldersView.js',
			pictureFoldersView : '/backbone/js/views/pictureFoldersView.js',
			podcastView : '/backbone/js/views/podcastsView.js'
		},
	callback : {
		"jqueryI18n": function () {
			// Initialize i18n
			$.i18n.properties({
			    name:'messages', 
			    path:'/backbone/bundle/',
			    mode:'map',
			    callback: function() {
			    	// Internationalize i18n elements
			    	$(".i18n").each( function(i,elem) {
			    		if ($(elem).data('msg') != undefined)
			    			$(elem).html(($.i18n.prop($(elem).data('msg'))));
			    		});
			    	}
				});
			console.log("i18n loaded ...");			
			}
		},
	complete : function () {
			console.log('Lauching application ...');
			
			window.defaultView = new Application.Views.DefaultView();

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
				podcasts:podcasts,
				defaultView:defaultView
			});
			
			Backbone.history.start();
			console.log('Application launched');

		}
});