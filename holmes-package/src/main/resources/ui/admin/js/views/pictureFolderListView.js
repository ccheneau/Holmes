var Application = (function(application) {
	application.Views.PictureFolderListView = Backbone.View.extend({
		el : $("#main_content"),
		initialize : function() {
			this.template = $.getTemplate("folderList.html");
			_.bindAll(this, 'render');
			this.collection.bind('reset', this.render);
			this.collection.bind('change', this.render);
			this.collection.bind('add', this.render);
			this.collection.bind('remove', this.render);
		},
		render : function() {
			var renderedContent = Mustache.to_html(this.template, {
				folders : this.collection.toJSON(),
				title : $.i18n.prop("msg.admin.picture.list.title"),
				description : $.i18n.prop("msg.admin.picture.list.description"),
				nameLabel : $.i18n.prop("msg.admin.name"),
				pathLabel : $.i18n.prop("msg.admin.path"),
				addLabel : $.i18n.prop("msg.admin.add"),
				editLabel : $.i18n.prop("msg.admin.edit"),
				removeLabel : $.i18n.prop("msg.admin.remove"),
				saveLabel : $.i18n.prop("msg.admin.save"),
				cancelLabel : $.i18n.prop("msg.admin.cancel"),
				browsable : true,
				dialogId : "pictureDlg",
				removeTarget : "pictureFolderRemove",
				icon : "picture"
			});
			this.$el.html(renderedContent);
			$(".pictureDlgEditOpen").tooltip({delay:{ show: 1000, hide: 0}});
			$(".pictureFolderRemove").tooltip({delay:{ show: 1000, hide: 0}});
			$(".pictureDlgAddOpen").tooltip({delay:{ show: 1000, hide: 0}});
		},
		events : {
			"click .pictureDlgAddOpen" : "onPictureDlgAddOpen",
			"click a.pictureDlgEditOpen" : "onPictureDlgEditOpen",
			"dblclick tr.pictureDlgEditOpen" : "onPictureDlgEditOpen",
			"click .pictureDlgClose" : "onPictureDlgClose",
			"click .pictureDlgSave" : "onPictureDlgSave",
			"click .pictureDlgBrowse" : "onPictureDlgBrowse",
			"click .pictureFolderRemove" : "onPictureFolderRemove",
		},
		// open add picture folder dialog
		onPictureDlgAddOpen : function() {
			// initialize dialog 
			$("#pictureDlgHeader").html($.i18n.prop("msg.admin.picture.add.title"));
			$("#folderId").val("");
			$("#folderName").val("");
			$("#folderPath").val("");
			this.showDialog();
			return false;
		},
		// open edit picture folder dialog
		onPictureDlgEditOpen : function(event) {
			var that = this;
			var folderId = $(event.currentTarget).data('id');
			// get picture folder
			var pictureFolder = new Application.Models.PictureFolder({id : folderId});
			pictureFolder.fetch({
				success : function(model) {
					// initialize dialog
					$("#pictureDlgHeader").html($.i18n.prop("msg.admin.picture.update.title"));
					$("#folderId").val(model.get('id'));
					$("#folderName").val(model.get('name'));
					$("#folderPath").val(model.get('path'));
					that.showDialog();
				},
				error : function(model,response) {
					bootbox.alert(response.responseText || response.statusText);
				}
			});
			return false;
		},
		// close dialog
		onPictureDlgClose : function() {
			folderSelectBox.hide();
			this.hideDialog();
			return false;
		},
		// save picture folder
		onPictureDlgSave : function() {
			var that = this;
			var folderId = $("#folderId").val();
			var folderName = $("#folderName").val().trim();
			var folderPath = $("#folderPath").val().trim();
			var pictureFolder;
			folderSelectBox.hide();
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
							// close dialog
							that.hideDialog();
							// fetch collection
							that.collection.fetch();
						},
						error : function(model, response) {
							$("#messagebox").message({text: response.responseText || response.statusText, type: "danger"});
						}
					});
			return false;
		},
		// Show browse dialog
		onPictureDlgBrowse : function (){
			folderSelectBox.show($("#folderPath"));
			return false;
		},
		// remove picture folder
		onPictureFolderRemove : function(event){
			var that = this;
			// confirm dialog
			bootbox.confirm($.i18n.prop("msg.admin.picture.remove.confirm"),function(result) {
				if (result == true) {
					var folderId = $(event.currentTarget).data('id');
					var pictureFolder = new Application.Models.PictureFolder({id : folderId});
					pictureFolder.destroy({
						success : function() {
							// fetch collection
							that.collection.fetch();
						},
						error : function(model, response) {
							bootbox.alert(response.responseText || response.statusText);
						}
					});
				}
			}); 
			return false;
		},
		showDialog : function(){
			$("#messagebox").html("");
			$('#pictureDlg').modal('show');
		},
		hideDialog : function(){
			$(".modal-backdrop").remove();
			$('#pictureDlg').modal('hide');
		}
	});
	return application;
}(Application));