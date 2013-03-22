var Application = (function(application) {
	application.Views.AudioFolderListView = Backbone.View.extend({
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
				title : $.i18n.prop("msg.audio.list.title"),
				nameLabel : $.i18n.prop("msg.name"),
				pathLabel : $.i18n.prop("msg.path"),
				addLabel : $.i18n.prop("msg.add"),
				editLabel : $.i18n.prop("msg.edit"),
				removeLabel : $.i18n.prop("msg.remove"),
				saveLabel : $.i18n.prop("msg.save"),
				cancelLabel : $.i18n.prop("msg.cancel"),
				browsable : true,
				dialogId : "audioDlg",
				removeTarget : "audioFolderRemove"
			});
			this.$el.html(renderedContent);
		},
		events : {
			"click .audioDlgAddOpen" : "onAudioDlgAddOpen",
			"click a.audioDlgEditOpen" : "onAudioDlgEditOpen",
			"dblclick tr.audioDlgEditOpen" : "onAudioDlgEditOpen",
			"click .audioDlgClose" : "onAudioDlgClose",
			"click .audioDlgSave" : "onAudioDlgSave",
			"click .audioDlgBrowse" : "onAudioDlgBrowse",
			"click .audioFolderRemove" : "onAudioFolderRemove",
		},
		// open add audio folder dialog
		onAudioDlgAddOpen : function() {
			// initialize dialog 
			$("#audioDlgHeader").html($.i18n.prop("msg.audio.add.title"));
			$("#folderId").val("");
			$("#folderName").val("");
			$("#folderPath").val("");
			this.showDialog();
			return false;
		},
		// open edit audio folder dialog
		onAudioDlgEditOpen : function(event) {
			var that = this;
			var folderId = $(event.currentTarget).data('id');
			// get audio folder
			var audioFolder = new Application.Models.AudioFolder({id : folderId});
			audioFolder.fetch({
				success : function(model) {
					// initialize dialog 
					$("#audioDlgHeader").html($.i18n.prop("msg.audio.update.title"));
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
		onAudioDlgClose : function() {
			folderSelectBox.hide();
			this.hideDialog();
			return false;
		},
		// save audio folder
		onAudioDlgSave : function() {
			var that = this;
			var folderId = $("#folderId").val();
			var folderName = $("#folderName").val().trim();
			var folderPath = $("#folderPath").val().trim();
			var audioFolder;
			folderSelectBox.hide();
			if (folderId === "") {
				// this is a new audio folder
				audioFolder = new Application.Models.AudioFolder();
			} else {
				// modify existing audio folder
				audioFolder = new Application.Models.AudioFolder({id:folderId});
			}
			// save audio folder
			audioFolder.save({
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
							$("#messagebox").message({text: response.responseText || response.statusText, type: "error"});
						}
					});
			return false;
		},
		// Show browse dialog
		onAudioDlgBrowse : function (){
			folderSelectBox.show($("#folderPath"));
			return false;
		},
		// remove audio folder
		onAudioFolderRemove : function(event){
			var that = this;
			// confirm dialog
			bootbox.confirm($.i18n.prop("msg.audio.remove.confirm"), $.i18n.prop("msg.no"),$.i18n.prop("msg.yes"),function(result) {
				if (result == true) {
					var folderId = $(event.currentTarget).data('id');
					var audioFolder = new Application.Models.AudioFolder({id : folderId});
					audioFolder.destroy({
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
			$("#audioDlg").draggable({
				handle: ".modal-header",
				start : function(event, ui) {ui.helper.removeClass('fade');}
			});
			$('#audioDlg').modal('show');
		},
		hideDialog : function(){
			$('#audioDlg').modal('hide');
		}
	});
	return application;
}(Application));