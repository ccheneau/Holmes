var Application = (function (application) {
	application.Views.PodcastsView = Backbone.View.extend({
		el : $("#podcast_list"),
		initialize : function () {
			this.template = $("#folder_list_template").html();
			_.bindAll(this, 'render');
			this.collection.bind('reset', this.render);
			this.collection.bind('change', this.render);
			this.collection.bind('add', this.render);
			this.collection.bind('remove', this.render);			
		},
		render : function () {
			var renderedContent = Mustache.to_html(this.template,
				{
					labelName : $.i18n.prop("msg.name"),
					labelPath : $.i18n.prop("msg.url"),
					title : $.i18n.prop("msg.podcast.title"),
					folders : this.collection.toJSON()
				}
			);
			this.$el.html(renderedContent);
		}
	});
	return application;
}(Application));