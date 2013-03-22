var Application = (function(application) {
	application.Views.DefaultView = Backbone.View.extend({
		el : $("#main_content"),
		initialize : function() {
			this.template = $.getTemplate("default.html");
		},
		render : function() {
			var renderedContent = Mustache.to_html(this.template, {
				defaultTitle : $.i18n.prop("msg.welcome.title"),
				defaultContent : $.i18n.prop("msg.welcome.content")
			});
			this.$el.html(renderedContent);
		}
	});
	return application;
}(Application));