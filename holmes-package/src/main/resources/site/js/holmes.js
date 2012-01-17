$.extend(
		// Default jqgrid options
		$.jgrid.defaults, { 
			datatype: "json", 
			height: 300,
			hidegrid: false,
			pgbuttons: false,
			pginput: false,
			viewrecords: false
		});

$(document).ready(function() {

		// Initialize tabs
		$( "#tabs" ).tabs();

		// Initialize video folders grid
		$("#list_video_folders").jqGrid({
			url:'/backend/configuration/getVideoFolders', 
			colNames:['ID','Label', 'Path'], 
			colModel:[ {name:"id",index:"id", width:0 , hidden:true, editable:false, sortable: false}, 
			           {name:"label",index:"label", width:150, editable:true, editrules:{required: true}, sortable: false}, 
			           {name:"path",index:"path", width:450, editable:true, editrules:{required: true}, editoptions: {size:70}, sortable: false} 
			          ], 
			caption: "Video folders",
			pager: '#list_video_folders_nav',
			editurl:"/backend/configuration/editVideoFolder" 
		});
		$("#list_video_folders").jqGrid('navGrid','#list_video_folders_nav', 
				{search: false}, //options 
				{height:150, width: 500, reloadAfterSubmit:true, editCaption: "Edit video folder", closeOnEscape:true, closeAfterEdit:true, afterSubmit: function(response,postdata){ return getEditResponseData(response);}}, // edit options 
				{height:150, width: 500, reloadAfterSubmit:true, addCaption: "Add video folder", closeOnEscape:true, closeAfterAdd:true, afterSubmit: function(response,postdata){ return getEditResponseData(response);}}, // add options 
				{reloadAfterSubmit:true, closeOnEscape:true, msg: "Delete selected video folder?", afterSubmit: function(response,postdata){ return getEditResponseData(response);}} // del options 
		);
		
		//Initialize audio folders grid
		$("#list_audio_folders").jqGrid({
			url:'/backend/configuration/getAudioFolders', 
			colNames:['ID','Label', 'Path'], 
			colModel:[ {name:"id",index:"id", width:0 , hidden:true, editable:false, sortable: false}, 
			           {name:"label",index:"label", width:150, editable:true, editrules:{required: true}, sortable: false}, 
			           {name:"path",index:"path", width:450, editable:true, editrules:{required: true}, editoptions: {size:70}, sortable: false} 
			          ], 
			caption: "Audio folders",
			pager: '#list_audio_folders_nav', 
			editurl:"/backend/configuration/editAudioFolder" 
		});
		$("#list_audio_folders").jqGrid('navGrid','#list_audio_folders_nav', 
				{search: false}, //options 
				{height:150, width: 500, reloadAfterSubmit:true, editCaption: "Edit audio folder", closeOnEscape:true, closeAfterEdit:true, afterSubmit: function(response,postdata){ return getEditResponseData(response);}}, // edit options 
				{height:150, width: 500, reloadAfterSubmit:true, addCaption: "Add audio folder", closeOnEscape:true, closeAfterAdd:true, afterSubmit: function(response,postdata){ return getEditResponseData(response);}}, // add options 
				{reloadAfterSubmit:true, closeOnEscape:true, msg: "Delete selected audio folder?", afterSubmit: function(response,postdata){ return getEditResponseData(response);}} // del options 
		);
		
		//Initialize picture folders grid
		$("#list_picture_folders").jqGrid({
			url:'/backend/configuration/getPictureFolders', 
			colNames:['ID','Label', 'Path'], 
			colModel:[ {name:"id",index:"id", width:0 , hidden:true, editable:false, sortable: false}, 
			           {name:"label",index:"label", width:150, editable:true, editrules:{required: true}, sortable: false}, 
			           {name:"path",index:"path", width:450, editable:true, editrules:{required: true}, editoptions: {size:70}, sortable: false} 
			          ], 
			caption: "Picture folders",
			pager: '#list_picture_folders_nav', 
			editurl:"/backend/configuration/editPictureFolder" 
		});
		$("#list_picture_folders").jqGrid('navGrid','#list_picture_folders_nav', 
				{search: false}, //options 
				{height:150, width: 500, reloadAfterSubmit:true, editCaption: "Edit picture folder", closeOnEscape:true, closeAfterEdit:true, afterSubmit: function(response,postdata){ return getEditResponseData(response);}}, // edit options 
				{height:150, width: 500, reloadAfterSubmit:true, addCaption: "Add picture folder", closeOnEscape:true, closeAfterAdd:true, afterSubmit: function(response,postdata){ return getEditResponseData(response);}}, // add options 
				{reloadAfterSubmit:true, closeOnEscape:true, msg: "Delete selected picture folder?", afterSubmit: function(response,postdata){ return getEditResponseData(response);}} // del options 
		);
		
		//Initialize podcasts rid
		$("#list_podcasts").jqGrid({
			url:'/backend/configuration/getPodcasts', 
			colNames:['ID','Label', 'URL'], 
			colModel:[ {name:"id",index:"id", width:0 , hidden:true, editable:false, sortable: false}, 
			           {name:"label",index:"label", width:150, editable:true, editrules:{required: true}, sortable: false}, 
			           {name:"path",index:"path", width:450, editable:true, editrules:{required: true}, editoptions: {size:70}, sortable: false} 
			          ], 
			caption: "Podcast URLs",
			pager: '#list_podcasts_nav', 
			editurl:"/backend/configuration/editPodcast" 
		});
		$("#list_podcasts").jqGrid('navGrid','#list_podcasts_nav', 
				{search: false}, //options 
				{height:150, width: 500, reloadAfterSubmit:true, editCaption: "Edit podcast", closeOnEscape:true, closeAfterEdit:true, afterSubmit: function(response,postdata){ return getEditResponseData(response);}}, // edit options 
				{height:150, width: 500, reloadAfterSubmit:true, addCaption: "Add podcast", closeOnEscape:true, closeAfterAdd:true, afterSubmit: function(response,postdata){ return getEditResponseData(response);}}, // add options 
				{reloadAfterSubmit:true, closeOnEscape:true, msg: "Delete selected podcast?", afterSubmit: function(response,postdata){ return getEditResponseData(response);}} // del options 
		);

		// Callback for edit/add/delete item server response
	    function getEditResponseData (response) {
	    	var serverResponse = $.parseJSON(response.responseText);
	    	if (serverResponse.operation == "edit") {
		        return [serverResponse.status,serverResponse.message, serverResponse.id];
	    	} else if (serverResponse.operation == "add") {
		        return [serverResponse.status,serverResponse.message, serverResponse.id];
	    	} else if (serverResponse.operation == "del") {
		        return [serverResponse.status,serverResponse.message];
	    	}
	        return [false,"Unable to parse response",""];
	    }

	    // Initialize configuration UI
	    $("#configuration_fieldset").addClass("ui-widget ui-widget-content ui-corner-all");
	    $("#text_server_name").addClass("ui-state-default ui-corner-all hover");
	    $("#text_http_server_port").addClass("ui-state-default ui-corner-all hover");
	    $("#select_log_level").addClass("ui-state-default ui-corner-all hover");
	    $("#btn_submit").addClass("fm-button ui-state-default ui-corner-all fm-button-icon-left hover");
	    $("#btn_submit").html($("#btn_submit").html() + "<span class='ui-icon ui-icon-disk'></span>");
	    $("#btn_reset").addClass("fm-button ui-state-default ui-corner-all fm-button-icon-left hover");
	    $("#btn_reset").html($("#btn_reset").html() + "<span class='ui-icon ui-icon-refresh'></span>");
	    $("#configuration_operation_fieldset").addClass("ui-widget ui-widget-content ui-corner-all");
	    $("#btn_scan_all").addClass("fm-button ui-state-default ui-corner-all fm-button-icon-left hover");
	    $("#btn_scan_all").html($("#btn_scan_all").html() + "<span class='ui-icon ui-icon-search'></span>");
	    
	    $(".hover").hover(function(){
			  $(this).addClass("ui-state-hover");
			   },function(){
			  $(this).removeClass("ui-state-hover");
		});
	    
	    $(".toolbar_hover").hover(function(){
			  $(this).addClass("toolbar-state-hover");
			   },function(){
			  $(this).removeClass("toolbar-state-hover");
		});
	    
	    // Initialize data
	    function loadConfiguration() {
		    $.getJSON('/backend/configuration/getConfiguration', function(response) {
		    		$("#text_server_name").val(response.serverName);
		    		$("#text_http_server_port").val(response.httpServerPort);
		    		$("#select_log_level").val(response.logLevel);
		    });
	    }
	    loadConfiguration();
	    
	    //Submit handler
	    $('#btn_submit').click(function() {
	    	$.post('/backend/configuration/editConfiguration',
	    			{serverName : $("#text_server_name").val(),
	    				httpServerPort : $("#text_http_server_port").val(),
	    				logLevel : $("#select_log_level").val()
	    			},
	    			function(response) {
	    				var serverResponse = $.parseJSON(response);
	    				if (serverResponse.status){
	    					successMessage('Configuration saved');
	    				} else {
	    					errorMessage(serverResponse.message);
	    				}
	    		});
	    });
	    
	    //Reset handler
	    $('#btn_reset').click(function() {
	    	closeMessage();
	    	loadConfiguration();
	    });
	    
	    //Scan all handler
	    $('#btn_scan_all').click(function() {
	    	$.post('/backend/configuration/launchScan',
	    			function(response) {
	    				var serverResponse = $.parseJSON(response);
	    				if (serverResponse.status){
	    					successMessage('Scan performed successfully');
	    				} else {
	    					errorMessage(serverResponse.message);
	    				}
	    		});
	    });	    

	    function successMessage(message) {
	    	$("#message").html("<span>" + message + "</span><a id='close_message' href='javascript:void(0)' style='float:right' class='ui-icon ui-icon-close'></a>");
	    	$('#close_message').click(function() { closeMessage(); });
	    }

	    function errorMessage(message) {
	    	$("#message").html("<span class='ui-state-error-text'>" + message + "</span><a id='close_message' href='javascript:void(0)' style='float:right' class='ui-icon ui-icon-close'></a>");
	    	$('#close_message').click(function() { closeMessage(); });
	    }

	    function closeMessage() {
	    	$('#close_message').unbind();
	    	$("#message").html("");
	    }
	});