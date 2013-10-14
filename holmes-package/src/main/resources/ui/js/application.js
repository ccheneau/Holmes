var Application = (function() {
	var application = {};
    application.version = "";
	application.Models = {};
	application.Collections = {};
	application.Views = {};
	application.Router = {};

    this._init = function(){
        // get Holmes version
        $.get('/backend/util/getVersion', function(response) {
            application.version = response;
            $("#version").html($.i18n.prop("msg.toolbar.version") + "&nbsp;" + response);
        });
    };
    this._init();

	// toggle nav menu
	toggleMenu = function(item) {
        $('#sidebar-nav a').removeClass('active');
        $('#'+ item).addClass('active');                
	},
	
	// router
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
			this.videoFolders.fetch({reset: true});
		},

		audioFolders : function() {
			toggleMenu('audio_folders_menu');
			this.audioFolders.fetch({reset: true});
		},
		pictureFolders : function() {
			toggleMenu('picture_folders_menu');
			this.pictureFolders.fetch({reset: true});
		},
		podcasts : function() {
			toggleMenu('podcasts_menu');
			this.podcasts.fetch({reset: true});
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