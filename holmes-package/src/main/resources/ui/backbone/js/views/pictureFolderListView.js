var Application = (function(application) {
	application.Views.PictureFolderListView = Backbone.View.extend({
		el : $("#main_content"),
		initialize : function() {
			this.template = application.getTemplate("folderList.html");
			_.bindAll(this, 'render');
			this.collection.bind('reset', this.render);
			this.collection.bind('change', this.render);
			this.collection.bind('add', this.render);
			this.collection.bind('remove', this.render);
		},
		render : function() {
			var renderedContent = Mustache.to_html(this.template, {
				folders : this.collection.toJSON(),
				title : $.i18n.prop("msg.picture.list.title"),
				nameLabel : $.i18n.prop("msg.name"),
				pathLabel : $.i18n.prop("msg.path"),
				addLabel : $.i18n.prop("msg.add"),
				editLabel : $.i18n.prop("msg.edit"),
				removeLabel : $.i18n.prop("msg.remove"),
				saveLabel : $.i18n.prop("msg.save"),
				cancelLabel : $.i18n.prop("msg.cancel"),
				dialogId : "pictureDlg",
				removeTarget : "pictureFolderRemove"
			});
			this.$el.html(renderedContent);
		},
		events : {
			"click .pictureDlgAddOpen" : "onPictureDlgAddOpen",
			"click .pictureDlgEditOpen" : "onPictureDlgEditOpen",
			"click .pictureDlgClose" : "onPictureDlgClose",
			"click .pictureDlgSave" : "onPictureDlgSave",
			"click .pictureFolderRemove" : "onPictureFolderRemove",
		},
		// open add picture folder dialog
		onPictureDlgAddOpen : function() {
			// initialiaze dialog 
			$("#pictureDlgHeader").html($.i18n.prop("msg.picture.add.title"));
			$("#folderId").val("");
			$("#folderName").val("");
			$("#folderPath").val("");
			$('#pictureDlg').modal('show');
			return false;
		},
		// open edit picture folder dialog
		onPictureDlgEditOpen : function(event) {
			var folderId = $(event.currentTarget).data('id');
			// get picture folder
			var pictureFolder = new Application.Models.PictureFolder({id : folderId});
			pictureFolder.fetch({
				success : function(result) {
					// initialiaze dialog 
					$("#pictureDlgHeader").html($.i18n.prop("msg.picture.update.title"));
					$("#folderId").val(result.get('id'));
					$("#folderName").val(result.get('name'));
					$("#folderPath").val(result.get('path'));
					$('#pictureDlg').modal('show');
				},
				error : function() {
					//TODO manage error
					alert("failed to edit");
				}
			});
			return false;
		},
		// close dialog
		onPictureDlgClose : function() {
			$('#pictureDlg').modal('hide');
			return false;
		},
		// save picture folder
		onPictureDlgSave : function() {
			var that = this;
			var folderId = $("#folderId").val();
			var folderName = $("#folderName").val();
			var folderPath = $("#folderPath").val();
			var pictureFolder;
			if (folderId === "") {
				// this is a new picture folder
				pictureFolder = new Application.Models.PictureFolder();
			} else {
				// modify existing picture folder
				pictureFolder = new Application.Models.PictureFolder({id:folderId});
			}
			// save picture folder
			pictureFolder.save({
						"name" : folderName,
						"path" : folderPath
					},{
						success : function() {
							that.collection.fetch();
						},
						error : function(model, response) {
							//TODO manage error
							alert("failed to save");
						}
					});
			// close dialog
			$('#pictureDlg').modal('hide');
			return false;
		},
		// remove picture folder
		onPictureFolderRemove : function(event){
			//TODO remove confirm
			if (confirm($.i18n.prop("msg.picture.remove.confirm"))) {
				var that = this;
				var folderId = $(event.currentTarget).data('id');
				var pictureFolder = new Application.Models.PictureFolder({id : folderId});
				pictureFolder.destroy({
					success : function() {
						that.collection.fetch();
					},
					error : function() {
						//TODO manage error
						alert("failed to remove");
					}
				});
			}
			return false;
		}
	});
	return application;
}(Application));