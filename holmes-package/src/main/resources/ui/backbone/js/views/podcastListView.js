var Application = (function(application) {
	application.Views.PodcastListView = Backbone.View.extend({
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
				title : $.i18n.prop("msg.podcast.list.title"),
				nameLabel : $.i18n.prop("msg.name"),
				pathLabel : $.i18n.prop("msg.url"),
				addLabel : $.i18n.prop("msg.add"),
				editLabel : $.i18n.prop("msg.edit"),
				removeLabel : $.i18n.prop("msg.remove"),
				saveLabel : $.i18n.prop("msg.save"),
				cancelLabel : $.i18n.prop("msg.cancel"),
				dialogId : "podcastDlg",
				removeTarget : "podcastRemove"
			});
			this.$el.html(renderedContent);
		},
		events : {
			"click .podcastDlgAddOpen" : "onPodcastDlgAddOpen",
			"click a.podcastDlgEditOpen" : "onPodcastDlgEditOpen",
			"dblclick tr.podcastDlgEditOpen" : "onPodcastDlgEditOpen",
			"click .podcastDlgClose" : "onPodcastDlgClose",
			"click .podcastDlgSave" : "onPodcastDlgSave",
			"click .podcastRemove" : "onPodcastRemove",
		},
		// open add podcast dialog
		onPodcastDlgAddOpen : function() {
			// initialiaze dialog 
			$("#podcastDlgHeader").html($.i18n.prop("msg.podcast.add.title"));
			$("#folderId").val("");
			$("#folderName").val("");
			$("#folderPath").val("");
			this.showDialog();
			return false;
		},
		// open edit podcast dialog
		onPodcastDlgEditOpen : function(event) {
			var that = this;
			var folderId = $(event.currentTarget).data('id');
			// get podcast
			var podcast = new Application.Models.Podcast({id : folderId});
			podcast.fetch({
				success : function(result) {
					// initialize dialog 
					$("#podcastDlgHeader").html($.i18n.prop("msg.podcast.update.title"));
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
		onPodcastDlgClose : function() {
			this.hideDialog();
			return false;
		},
		// save podcast
		onPodcastDlgSave : function() {
			var that = this;
			var folderId = $("#folderId").val();
			var folderName = $("#folderName").val().trim();
			var folderPath = $("#folderPath").val().trim();
			var podcast;
			if (folderId === "") {
				// this is a new podcast
				podcast = new Application.Models.Podcast();
			} else {
				// modify existing podcast
				podcast = new Application.Models.Podcast({id:folderId});
			}
			// save podcast
			podcast.save({
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
		// remove podcast
		onPodcastRemove : function(event){
			var that = this;
			// confirm dialog
			bootbox.confirm($.i18n.prop("msg.podcast.remove.confirm"), $.i18n.prop("msg.no"),$.i18n.prop("msg.yes"),function(result) {
				if (result == true) {
					var folderId = $(event.currentTarget).data('id');
					var podcast = new Application.Models.Podcast({id : folderId});
					podcast.destroy({
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
			$("#podcastDlg").draggable({
				handle: ".modal-header",
				start : function(event, ui) {ui.helper.removeClass('fade');}
			});
			$('#podcastDlg').modal('show');
		},
		hideDialog : function(){
			$('#podcastDlg').modal('hide');
		}
	});
	return application;
}(Application));