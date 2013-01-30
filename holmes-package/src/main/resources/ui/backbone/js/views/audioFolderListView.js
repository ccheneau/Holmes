var Application = (function(application) {
	application.Views.AudioFolderListView = Backbone.View.extend({
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
				title : $.i18n.prop("msg.audio.list.title"),
				nameLabel : $.i18n.prop("msg.name"),
				pathLabel : $.i18n.prop("msg.path"),
				addLabel : $.i18n.prop("msg.add"),
				editLabel : $.i18n.prop("msg.edit"),
				removeLabel : $.i18n.prop("msg.remove"),
				editTarget : "editAudioFolder",
				addTarget : "addAudioFolder",
				removeTarget : "removeAudioFolder"
			});
			this.$el.html(renderedContent);
			$("#admin_content").html("");
		}
	});
	return application;
}(Application));