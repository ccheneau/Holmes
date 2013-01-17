var Application = (function (application) {
	application.Views.DefaultView = Backbone.View.extend({
		el : $("#main_content"),
		initialize : function () {
			this.template = $("#index_template").html();
		},
		render : function () {
			var renderedContent = Mustache.to_html(this.template,
				{
					indexMessage :$.i18n.prop("msg.welcome")
				}
			);
			this.$el.html(renderedContent);
		}
	});
	return application;
}(Application));