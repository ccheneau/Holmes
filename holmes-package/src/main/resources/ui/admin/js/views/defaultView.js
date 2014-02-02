var Application = (function(application) {
	application.Views.DefaultView = Backbone.View.extend({
		el : $("#main_content"),
		initialize : function() {
			this.template = $.getTemplate("default.html");
		},
		render : function() {
			var renderedContent = Mustache.to_html(this.template, {
				title : $.i18n.prop("msg.admin.welcome.title"),
				description : $.i18n.prop("msg.admin.welcome.description")
			});
			this.$el.html(renderedContent);
		}
	});
	return application;
}(Application));