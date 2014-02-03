yepnope({
	load : {
		// lib
		jquery : '/js/jquery-2.1.0.min.js',
		jqueryUI : '/js/jquery-ui-1.10.4.custom.min.js',
		bootstrap : '/js/bootstrap.min.js',
		jqueryI18n : '/js/jquery.i18n.properties-min-1.0.9.js',
		underscore : '/js/underscore-min.js',
		backbone : '/js/backbone-min.js',
		mustache : '/js/mustache.js',
		bootbox : '/js/bootbox-4.1.0.min.js',
		jstree : '/js/jquery.jstree.js',
		
		// plugins
		folderSelectBox : '/admin/js/plugins/folder-select-box.js',
		message : '/admin/js/plugins/jq-message.js',
		template : '/admin/js/plugins/jq-template.js',
		logger : '/js/plugins/jq-logger.js',
		
		// application
		application : '/admin/js/application.js',

		// models
		videoFolder : '/admin/js/models/videoFolder.js',
		audioFolder : '/admin/js/models/audioFolder.js',
		pictureFolder : '/admin/js/models/pictureFolder.js',
		podcast : '/admin/js/models/podcast.js',
		settings : '/admin/js/models/settings.js',

		// view controllers
		defaultView : '/admin/js/views/defaultView.js',
		settingsView : '/admin/js/views/settingsView.js',
		videoFolderListView : '/admin/js/views/videoFolderListView.js',
		audioFolderListView : '/admin/js/views/audioFolderListView.js',
		pictureFolderListView : '/admin/js/views/pictureFolderListView.js',
		podcastListView : '/admin/js/views/podcastListView.js',
	},
	callback : {
		"jquery" : function() {
		    $.ajaxSetup({ cache: false });
		},
		"jqueryI18n" : function() {
			// initialize i18n
			$.i18n.properties({
				name : 'messages',
				path : '/bundle/',
				mode : 'map',
				callback : function() {
					// Internationalize i18n elements
					$(".i18n").each(function(i, elem) {
						if ($(elem).data('msg') != undefined)
							$(elem).html(($.i18n.prop($(elem).data('msg'))));
					});
				}
			});
		},
		"folderSelectBox" : function() {
			folderSelectBox.init('/backend/util/getChildFolders',
					$.i18n.prop("msg.admin.select.folder.title"), $.i18n.prop("msg.admin.cancel"), $.i18n.prop("msg.admin.ok"));
		}
	},
	complete : function() {
		$.logger('Launching Holmes UI ...');

        bootbox.setDefaults({locale: $.i18n.prop('locale'),  className: 'overflowHidden'});

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

		$.logger('Holmes UI launched');
	}
});