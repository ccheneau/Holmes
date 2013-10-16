var Application = (function(application) {
	application.Views.VideoFolderListView = Backbone.View.extend({
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
				title : $.i18n.prop("msg.video.list.title"),
				description : $.i18n.prop("msg.video.list.description"),
				nameLabel : $.i18n.prop("msg.name"),
				pathLabel : $.i18n.prop("msg.path"),
				addLabel : $.i18n.prop("msg.add"),
				editLabel : $.i18n.prop("msg.edit"),
				removeLabel : $.i18n.prop("msg.remove"),
				saveLabel : $.i18n.prop("msg.save"),
				cancelLabel : $.i18n.prop("msg.cancel"),
				browsable : true,
				dialogId : "videoDlg",
				removeTarget : "videoFolderRemove",
				icon : "film"
			});
			this.$el.html(renderedContent);
			$(".videoDlgEditOpen").tooltip({delay:{ show: 1000, hide: 0}});
			$(".videoFolderRemove").tooltip({delay:{ show: 1000, hide: 0}});
			$(".videoDlgAddOpen").tooltip({delay:{ show: 1000, hide: 0}});
		},
		events : {
			"click .videoDlgAddOpen" : "onVideoDlgAddOpen",
			"click a.videoDlgEditOpen" : "onVideoDlgEditOpen",
			"dblclick tr.videoDlgEditOpen" : "onVideoDlgEditOpen",
			"click .videoDlgClose" : "onVideoDlgClose",
			"click .videoDlgSave" : "onVideoDlgSave",
			"click .videoDlgBrowse" : "onVideoDlgBrowse",
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
					// initialize dialog
					$("#videoDlgHeader").html($.i18n.prop("msg.video.update.title"));
					$("#folderId").val(result.get('id'));
					$("#folderName").val(result.get('name'));
					$("#folderPath").val(result.get('path'));
					that.showDialog();
				},
				error : function(model,response) {
					bootbox.alert(response.responseText || response.statusText);
				}
			});
			return false;
		},
		// close dialog
		onVideoDlgClose : function() {
			folderSelectBox.hide();
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
			folderSelectBox.hide();
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
							$("#messagebox").message({text: response.responseText || response.statusText, type: "danger"});
						}
					});
			return false;
		},
		// Show browse dialog
		onVideoDlgBrowse : function (){
			folderSelectBox.show($("#folderPath"));
			return false;
		},
		// remove video folder
		onVideoFolderRemove : function(event){
			var that = this;
			// confirm dialog
			bootbox.confirm($.i18n.prop("msg.video.remove.confirm"),function(result) {
				if (result == true) {
					var folderId = $(event.currentTarget).data('id');
					var videoFolder = new Application.Models.VideoFolder({id : folderId});
					videoFolder.destroy({
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
			$("#videoDlg").draggable({
				handle: ".modal-header",
				start : function(event, ui) {ui.helper.removeClass('fade');}
			});
			$('#videoDlg').modal('show');
		},
		hideDialog : function(){
			$(".modal-backdrop").remove();
			$("body").removeClass("modal-open");
			$('#videoDlg').modal('hide');
		}
	});
	return application;
}(Application));