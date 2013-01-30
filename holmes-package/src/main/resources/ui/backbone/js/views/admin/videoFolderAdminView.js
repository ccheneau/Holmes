var Application = (function (application) {
	application.Views.VideoFolderAdminView = Backbone.View.extend({
		el : $("#admin_content"),
		initialize : function() {
			this.template = $("#folder_admin_template").html();
		},
		render : function(videoFolder) {
			var renderedContent = Mustache.to_html(this.template,
				{
					folder : videoFolder.toJSON(),
					nameLabel : $.i18n.prop("msg.name"),
					pathLabel : $.i18n.prop("msg.path"),
					addTitle : $.i18n.prop("msg.video.add.title"),
					updateTitle : $.i18n.prop("msg.video.update.title"),
					addLabel : $.i18n.prop("msg.add"),
					saveLabel : $.i18n.prop("msg.save"),
					editLabel : $.i18n.prop("msg.edit"),
					cancelLabel : $.i18n.prop("msg.cancel"),
					cancelTarget : "videoFolders",
					sendTarget : "sendVideoFolder",
				}
			);
			this.$el.html(renderedContent);
		},
		events : {
			"click #sendVideoFolder" : "onSendVideoFolder"
		},
		onSendVideoFolder : function() {
			var that = this;
			var folderId = $("#admin_content > [name='folderId']").val();
			var folderLabel = $("#admin_content > [name='folderLabel']").val();
			var folderPath = $("#admin_content > [name='folderPath']").val();
			var videoFolder;
			if (folderId === "") {
				videoFolder = new Application.Models.VideoFolder();
			} else {
				videoFolder = new Application.Models.VideoFolder({id:folderId});
			}
			
			videoFolder.save({
						"name" : folderLabel,
						"path" : folderPath
					},{
						success : function() {
							that.collection.fetch();
						},
						error : function(model, response) {
							console.log("video save error");
							console.log(model);
							console.log(response);
						}
					});
			return false;
		}
	});
	return application;
}(Application));