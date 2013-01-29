var Application = (function (application) {
	application.Views.PodcastAdminView = Backbone.View.extend({
		el : $("#admin_content"),
		initialize : function() {
			this.template = $("#folder_admin_template").html();
		},
		render : function(podcast) {
			var renderedContent = Mustache.to_html(this.template,
				{
					folder : podcast.toJSON(),
					nameLabel : $.i18n.prop("msg.name"),
					pathLabel : $.i18n.prop("msg.path"),
					addTitle : $.i18n.prop("msg.podcast.add.title"),
					updateTitle : $.i18n.prop("msg.podcast.update.title"),
					addLabel : $.i18n.prop("msg.add"),
					saveLabel : $.i18n.prop("msg.save"),
					editLabel : $.i18n.prop("msg.edit"),
					cancelLabel : $.i18n.prop("msg.cancel"),
					cancelTarget : "podcasts",
					sendTarget : "sendPodcast",
				}
			);
			this.$el.html(renderedContent);
		},
		events : {
			"click #sendPodcast" : "onSendPodcast"
		},
		onSendPodcast : function() {
			var that = this;
			var folderId = $("#admin_content > [name='folderId']").val();
			var folderLabel = $("#admin_content > [name='folderLabel']").val();
			var folderPath = $("#admin_content > [name='folderPath']").val();
			var podcast;
			if (folderId === "") {
				podcast = new Application.Models.Podcast();
			} else {
				podcast = new Application.Models.Podcast({id:folderId});
			}
			
			podcast.save({
						"name" : folderLabel,
						"path" : folderPath
					},{
						success : function() {
							that.collection.fetch();
						},
						error : function(model, response) {
							console.log("podcast save error");
							console.log(model);
							console.log(response);
						}
					});
		}
	});
	return application;
}(Application));