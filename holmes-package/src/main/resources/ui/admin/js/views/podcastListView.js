var Application = (function(application) {
	application.Views.PodcastListView = Backbone.View.extend({
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
				title : $.i18n.prop("msg.admin.podcast.list.title"),
				description : $.i18n.prop("msg.admin.podcast.list.description"),
				nameLabel : $.i18n.prop("msg.admin.name"),
				pathLabel : $.i18n.prop("msg.admin.url"),
				addLabel : $.i18n.prop("msg.admin.add"),
				editLabel : $.i18n.prop("msg.admin.edit"),
				removeLabel : $.i18n.prop("msg.admin.remove"),
				saveLabel : $.i18n.prop("msg.admin.save"),
				cancelLabel : $.i18n.prop("msg.admin.cancel"),
				dialogId : "podcastDlg",
				removeTarget : "podcastRemove",
				icon : "list"
			});
			this.$el.html(renderedContent);
			$(".podcastDlgEditOpen").tooltip({delay:{ show: 1000, hide: 0}});
			$(".podcastRemove").tooltip({delay:{ show: 1000, hide: 0}});
			$(".podcastDlgAddOpen").tooltip({delay:{ show: 1000, hide: 0}});
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
			// initialize dialog
			$("#podcastDlgHeader").html($.i18n.prop("msg.admin.podcast.add.title"));
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
					$("#podcastDlgHeader").html($.i18n.prop("msg.admin.podcast.update.title"));
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
							$("#messagebox").message({text: response.responseText || response.statusText, type: "danger"});
						}
					});
			return false;
		},
		// remove podcast
		onPodcastRemove : function(event){
			var that = this;
			// confirm dialog
			bootbox.confirm($.i18n.prop("msg.admin.podcast.remove.confirm"),function(result) {
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
			$('#podcastDlg').modal('show');
		},
		hideDialog : function(){
			$(".modal-backdrop").remove();
			$('#podcastDlg').modal('hide');
		}
	});
	return application;
}(Application));