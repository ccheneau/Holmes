var Application = (function (application) {
	application.Router.RoutesManager = Backbone.Router.extend({
		initialize : function(args) {
			this.videoFolders = args.videoFolders;
			this.audioFolders = args.audioFolders;
			this.pictureFolders = args.pictureFolders;
			this.podcasts = args.podcasts;
		},
		
		routes : {
			"*path" : "root"
		},
		
		root : function () {
			this.videoFolders.all().fetch({
				success:function(result){
					//ça marche 
				}
			});
			this.audioFolders.all().fetch({
				success:function(result){
					//ça marche 
				}
			});
			this.pictureFolders.all().fetch({
				success:function(result){
					//ça marche 
				}
			});
			this.podcasts.all().fetch({
				success:function(result){
					//ça marche 
				}
			});
		},
	});
	return application;
}(Application));