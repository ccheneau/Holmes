var Application = (function(application) {
	application.Views.DefaultView = Backbone.View.extend({
		el : $("#main_content"),
		initialize : function() {
			this.template = $.getTemplate("default.html");
		},
		render : function() {
		    var needsUpdate = false;
		    var releaseMessage = '';
		    if (application.releaseInfo.needsUpdate) {
		        needsUpdate = true;
		        releaseMessage = $.i18n.prop("msg.admin.welcome.newRelease",application.releaseInfo.url, application.releaseInfo.name);
		    }
			var renderedContent = Mustache.to_html(this.template, {
				title : $.i18n.prop("msg.admin.welcome.title"),
				description : $.i18n.prop("msg.admin.welcome.description"),
				needsUpdate : needsUpdate,
				releaseMessage : releaseMessage
			});
			this.$el.html(renderedContent);
		}
	});
	return application;
}(Application));