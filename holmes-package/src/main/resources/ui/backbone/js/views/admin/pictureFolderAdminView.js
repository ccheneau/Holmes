var Application = (function (application) {
	application.Views.PictureFolderAdminView = Backbone.View.extend({
		el : $("#admin_content"),
		initialize : function() {
			this.template = $("#folder_admin_template").html();
		},
		render : function(pictureFolder) {
			var renderedContent = Mustache.to_html(this.template,
				{
					folder : pictureFolder.toJSON(),
					nameLabel : $.i18n.prop("msg.name"),
					pathLabel : $.i18n.prop("msg.path"),
					addTitle : $.i18n.prop("msg.picture.add.title"),
					updateTitle : $.i18n.prop("msg.picture.update.title"),
					addLabel : $.i18n.prop("msg.add"),
					saveLabel : $.i18n.prop("msg.save"),
					editLabel : $.i18n.prop("msg.edit"),
					cancelLabel : $.i18n.prop("msg.cancel"),
					cancelTarget : "pictureFolders",
					sendTarget : "sendPictureFolder",
				}
			);
			this.$el.html(renderedContent);
		},
		events : {
			"click #sendPictureFolder" : "onSendPictureFolder"
		},
		onSendPictureFolder : function() {
			var that = this;
			var folderId = $("#admin_content > [name='folderId']").val();
			var folderLabel = $("#admin_content > [name='folderLabel']").val();
			var folderPath = $("#admin_content > [name='folderPath']").val();
			var pictureFolder;
			if (folderId === "") {
				pictureFolder = new Application.Models.PictureFolder();
			} else {
				pictureFolder = new Application.Models.PictureFolder({id:folderId});
			}
			
			pictureFolder.save({
						"name" : folderLabel,
						"path" : folderPath
					},{
						success : function() {
							that.collection.fetch();
						},
						error : function(model, response) {
							console.log("save error");
							console.log(model);
							console.log(response);
						}
					});
			return false;
		}
	});
	return application;
}(Application));