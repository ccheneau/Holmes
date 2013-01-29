var Application = (function (application) {
	application.Views.AudioFolderAdminView = Backbone.View.extend({
		el : $("#admin_content"),
		initialize : function() {
			this.template = $("#folder_admin_template").html();
		},
		render : function(audioFolder) {
			var renderedContent = Mustache.to_html(this.template,
				{
					folder : audioFolder.toJSON(),
					nameLabel : $.i18n.prop("msg.name"),
					pathLabel : $.i18n.prop("msg.path"),
					addTitle : $.i18n.prop("msg.audio.add.title"),
					updateTitle : $.i18n.prop("msg.audio.update.title"),
					addLabel : $.i18n.prop("msg.add"),
					saveLabel : $.i18n.prop("msg.save"),
					editLabel : $.i18n.prop("msg.edit"),
					cancelLabel : $.i18n.prop("msg.cancel"),
					cancelTarget : "audioFolders",
					sendTarget : "sendAudioFolder",
				}
			);
			this.$el.html(renderedContent);
		},
		events : {
			"click #sendAudioFolder" : "onSendAudioFolder"
		},
		onSendAudioFolder : function() {
			var that = this;
			var folderId = $("#admin_content > [name='folderId']").val();
			var folderLabel = $("#admin_content > [name='folderLabel']").val();
			var folderPath = $("#admin_content > [name='folderPath']").val();
			var audioFolder;
			if (folderId === "") {
				audioFolder = new Application.Models.AudioFolder();
			} else {
				audioFolder = new Application.Models.AudioFolder({id:folderId});
			}
			
			audioFolder.save({
						"name" : folderLabel,
						"path" : folderPath
					},{
						success : function() {
							that.collection.fetch();
						},
						error : function(model, response) {
							console.log("audio save error");
							console.log(model);
							console.log(response);
						}
					});
		}
	});
	return application;
}(Application));