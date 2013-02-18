var Application = (function() {
	var application = {};
	application.Models = {};
	application.Collections = {};
	application.Views = {};
	application.Router = {};

	// get view template
	application.getTemplate = function(template) {
		return $.ajax({
			type : "GET",
			url : "/backbone/templates/" + template,
			async : false,
			cache : true
		}).responseText;
	},

	// toggle nav menu
	toggleMenu = function(item) {
        $('ul.nav > li').removeClass('active');
        $('#'+ item).addClass('active');                
	},
	
	// implements router
	application.Router.RoutesManager = Backbone.Router.extend({
		initialize : function(args) {
			this.videoFolders = args.videoFolders;
			this.audioFolders = args.audioFolders;
			this.pictureFolders = args.pictureFolders;
			this.podcasts = args.podcasts;
			this.defaultView = args.defaultView;
			this.settingsView = args.settingsView;
		},
		routes : {
			"videoFolders" : "videoFolders",
			"audioFolders" : "audioFolders",
			"pictureFolders" : "pictureFolders",
			"podcasts" : "podcasts",
			"settings" : "settings",
			"*path" : "root"
		},

		videoFolders : function() {
			toggleMenu('video_folders_menu');
			this.videoFolders.all().fetch();
		},

		audioFolders : function() {
			toggleMenu('audio_folders_menu');
			this.audioFolders.all().fetch();
		},
		pictureFolders : function() {
			toggleMenu('picture_folders_menu');
			this.pictureFolders.all().fetch();
		},
		podcasts : function() {
			toggleMenu('podcasts_menu');
			this.podcasts.all().fetch();
		},
		settings : function() {
			toggleMenu('settings_menu');
			this.settingsView.render();			
		},
		root : function() {
			toggleMenu('home_menu');
			this.defaultView.render();
		},
	});

	return application;
}());