var Application = (function(application) {
	application.Views.SettingsView = Backbone.View.extend({
		el : $("#main_content"),
		initialize : function() {
			this.template = application.getTemplate("settings.html");
		},
		render : function() {
			var renderedContent = Mustache.to_html(this.template, {
				title : $.i18n.prop("msg.settings.title"),
				settingsServerName : $.i18n.prop("msg.settings.serverName"),
				settingsServerPort : $.i18n.prop("msg.settings.httpServerPort"),
				settingsPrependPodcastItem : $.i18n.prop("msg.settings.prependPodcastItem"),
				cancel : $.i18n.prop("msg.cancel"),
				save : $.i18n.prop("msg.save")
			});
			this.$el.html(renderedContent);
		}
	});
	return application;
}(Application));