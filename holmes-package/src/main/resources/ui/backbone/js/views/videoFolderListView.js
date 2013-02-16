var Application = (function(application) {
	application.Views.VideoFolderListView = Backbone.View.extend({
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
				title : $.i18n.prop("msg.video.list.title"),
				nameLabel : $.i18n.prop("msg.name"),
				pathLabel : $.i18n.prop("msg.path"),
				addLabel : $.i18n.prop("msg.add"),
				editLabel : $.i18n.prop("msg.edit"),
				removeLabel : $.i18n.prop("msg.remove"),
				saveLabel : $.i18n.prop("msg.save"),
				cancelLabel : $.i18n.prop("msg.cancel"),
				dialogId : "videoDlg",
				removeTarget : "videoFolderRemove"
			});
			this.$el.html(renderedContent);
		},
		events : {
			"click .videoDlgAddOpen" : "onVideoDlgAddOpen",
			"click .videoDlgEditOpen" : "onVideoDlgEditOpen",
			"click .videoDlgClose" : "onVideoDlgClose",
			"click .videoDlgSave" : "onVideoDlgSave",
			"click .videoFolderRemove" : "onVideoFolderRemove",
		},
		// open add video folder dialog
		onVideoDlgAddOpen : function() {
			// initialize dialog 
			$("#videoDlgHeader").html($.i18n.prop("msg.video.add.title"));
			$("#folderId").val("");
			$("#folderName").val("");
			$("#folderPath").val("");
			this.showDialog();
			return false;
		},
		// open edit video folder dialog
		onVideoDlgEditOpen : function(event) {
			var that = this;
			var folderId = $(event.currentTarget).data('id');
			// get video folder
			var videoFolder = new Application.Models.VideoFolder({id : folderId});
			videoFolder.fetch({
				success : function(result) {
					// initialiaze dialog 
					$("#videoDlgHeader").html($.i18n.prop("msg.video.update.title"));
					$("#folderId").val(result.get('id'));
					$("#folderName").val(result.get('name'));
					$("#folderPath").val(result.get('path'));
					that.showDialog();
				},
				error : function(model,response) {
					bootbox.alert(response.responseText);
				}
			});
			return false;
		},
		// close dialog
		onVideoDlgClose : function() {
			this.hideDialog();
			return false;
		},
		// save video folder
		onVideoDlgSave : function() {
			var that = this;
			var folderId = $("#folderId").val();
			var folderName = $("#folderName").val().trim();
			var folderPath = $("#folderPath").val().trim();
			var videoFolder;
			if (folderId === "") {
				// this is a new video folder
				videoFolder = new Application.Models.VideoFolder();
			} else {
				// modify existing video folder
				videoFolder = new Application.Models.VideoFolder({id:folderId});
			}
			// save video folder
			videoFolder.save({
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
							$("#messagebox").message({text: response.responseText, type: "error"});
						}
					});
			return false;
		},
		// remove video folder
		onVideoFolderRemove : function(event){
			var that = this;
			// confirm dialog
			bootbox.confirm($.i18n.prop("msg.video.remove.confirm"), $.i18n.prop("msg.no"),$.i18n.prop("msg.yes"),function(result) {
				if (result == true) {
					var folderId = $(event.currentTarget).data('id');
					var videoFolder = new Application.Models.VideoFolder({id : folderId});
					videoFolder.destroy({
						success : function() {
							// fetch collection
							that.collection.fetch();
						},
						error : function(model, response) {
							bootbox.alert(response.responseText);
						}
					});
				}
			}); 
			return false;
		},
		showDialog : function(){
			$("#messagebox").html("");
			$("#videoDlg").draggable({
				handle: ".modal-header",
				start : function(event, ui) {ui.helper.removeClass('fade');}
			});
			$('#videoDlg').modal('show');
		},
		hideDialog : function(){
			$('#videoDlg').modal('hide');
		}
	});
	return application;
}(Application));