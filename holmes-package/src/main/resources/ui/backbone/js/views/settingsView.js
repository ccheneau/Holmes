var Application = (function(application) {
	application.Views.SettingsView = Backbone.View.extend({
		el : $("#main_content"),
		url : '/backend/backbone/settings',
		initialize : function() {
			this.template = application.getTemplate("settings.html");
		},
		render : function() {
			var that = this;
		    $.getJSON(this.url, function(response) {
				var renderedContent = Mustache.to_html(that.template, {
					settings : response,
					title : $.i18n.prop("msg.settings.title"),
					settingsServerName : $.i18n.prop("msg.settings.serverName"),
					settingsServerPort : $.i18n.prop("msg.settings.httpServerPort"),
					settingsPrependPodcastItem : $.i18n.prop("msg.settings.prependPodcastItem"),
					cancel : $.i18n.prop("msg.cancel"),
					save : $.i18n.prop("msg.save")
				});
				that.$el.html(renderedContent);
		    });
		},
		events : {
			"click #btnSettingsSave" : "onSettingsSave",
			"click #btnSettingsCancel" : "onSettingsCancel"
		},
		onSettingsSave : function() {
			var that = this;
	    	$.post(this.url,
	        		{serverName : $("#settingsServerName").val(),
	        			httpServerPort : $("#settingsHttpServerPort").val(),
	        			prependPodcastItem : $("#chkPrependPodcastItem").is(':checked') ? "true":"false"
	        		})
	        		.done(function() { that.render()})
	        		.fail(function() { alert("error"); });
		},
		onSettingsCancel : function() {
			this.render();
		}
	});
	return application;
}(Application));