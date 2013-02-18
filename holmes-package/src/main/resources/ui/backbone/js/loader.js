yepnope({
	load : {
		// lib
		jquery : '/backbone/js/lib/jquery-1.9.1.min.js',
		jqueryUI : '/backbone/js/lib/jquery-ui-1.10.0.custom.min.js',
		bootstrap : '/backbone/js/lib/bootstrap.min.js',
		jqueryI18n : '/backbone/js/lib/jquery.i18n.properties-min-1.0.9.js',
		underscore : '/backbone/js/lib/underscore-min.js',
		backbone : '/backbone/js/lib/backbone-min.js',
		mustache : '/backbone/js/lib/mustache.js',
		bootbox : '/backbone/js/lib/bootbox.min.js',
		jstree : '/backbone/js/lib/jquery.jstree.js',
		
		// plugins
		message : '/backbone/js/plugins/message.js',
		folderSelectBox : '/backbone/js/plugins/folder-select-box.js',
		
		// application
		application : '/backbone/js/application.js',

		// models
		videoFolder : '/backbone/js/models/videoFolder.js',
		audioFolder : '/backbone/js/models/audioFolder.js',
		pictureFolder : '/backbone/js/models/pictureFolder.js',
		podcast : '/backbone/js/models/podcast.js',

		// view controllers
		defaultView : '/backbone/js/views/defaultView.js',
		settingsView : '/backbone/js/views/settingsView.js',
		videoFolderListView : '/backbone/js/views/videoFolderListView.js',
		audioFolderListView : '/backbone/js/views/audioFolderListView.js',
		pictureFolderListView : '/backbone/js/views/pictureFolderListView.js',
		podcastListView : '/backbone/js/views/podcastListView.js',
	},
	callback : {
		"jquery" : function() {
		    $('#easter').click(function() {
		    	$('body').attr('class','roll');
		    });
		},
		"jqueryI18n" : function() {
			// initialize i18n
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
			
			// get Holmes version
		    $.get('/backend/util/getVersion', function(response) {
		    	$("#version").html($.i18n.prop("msg.toolbar.version") + "&nbsp;" + response);
		    });
		},
		"folderSelectBox" : function() {
			folderSelectBox.init('/backend/util/getChildFolders',
					$.i18n.prop("msg.select.folder.title"), $.i18n.prop("msg.cancel"), $.i18n.prop("msg.ok"));
		}
	},
	complete : function() {
		console.log('Launching Holmes UI ...');

		window.defaultView = new Application.Views.DefaultView();
		window.settingsView = new Application.Views.SettingsView();

		window.videoFolders = new Application.Collections.VideoFolders();
		window.videoFolderListView = new Application.Views.VideoFolderListView({
			collection : videoFolders
		});

		window.audioFolders = new Application.Collections.AudioFolders();
		window.audioFolderListView = new Application.Views.AudioFolderListView({
			collection : audioFolders
		});

		window.pictureFolders = new Application.Collections.PictureFolders();
		window.pictureFolderListView = new Application.Views.PictureFolderListView({
			collection : pictureFolders
		});

		window.podcasts = new Application.Collections.Podcasts();
		window.podcastListView = new Application.Views.PodcastListView({
			collection : podcasts
		});

		window.router = new Application.Router.RoutesManager({
			videoFolders : videoFolders,
			audioFolders : audioFolders,
			pictureFolders : pictureFolders,
			podcasts : podcasts,
			defaultView : defaultView,
			settingsView : settingsView
		});

		Backbone.history.start();
		console.log('Holmes UI launched');
	}
});