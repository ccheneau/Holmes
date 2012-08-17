/* Copyright (C) 2012  Cedric Cheneau
* 
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
* 
* You should have received a copy of the GNU General Public License
* along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

$(document).ready(function() {
		// Initialize tabs
		$( "#tabs" ).tabs();

		// Initialize i18n
		$.i18n.properties({
		    name:'messages', 
		    path:'bundle/',
		    mode:'both',
		    callback: function() {
		    	// HTML internationalization
		    	$(".i18n").each( function(i,elem) {
		    		if ($(elem).data('msg') != undefined)
		    			$(elem).html(($.i18n.prop($(elem).data('msg'))));
		    	});
		    	// Initialize document
	    		initializeDocument();
		    }
		});
		
	    // Initialize configuration UI
	    $("#configuration_fieldset").addClass("ui-widget ui-widget-content ui-corner-all");
	    $("#text_server_name").addClass("ui-state-default ui-corner-all hover");
	    $("#text_http_server_port").addClass("ui-state-default ui-corner-all hover");
	    $("#btn_submit").addClass("fm-button ui-state-default ui-corner-all fm-button-icon-left hover");
	    $("#btn_submit").html($("#btn_submit").html() + "<span class='ui-icon ui-icon-disk'></span>");
	    $("#btn_reset").addClass("fm-button ui-state-default ui-corner-all fm-button-icon-left hover");
	    $("#btn_reset").html($("#btn_reset").html() + "<span class='ui-icon ui-icon-refresh'></span>");
	    
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
	    
	    // Load configuration data
	    loadConfiguration();
	    
	    // Bind configuration submit handler
	    $('#btn_submit').click(function() {
	    	$.post('/backend/configuration/editConfiguration',
	    			{serverName : $("#text_server_name").val(),
	    				httpServerPort : $("#text_http_server_port").val()
	    			},
	    			function(response) {
	    				if (response.status){
	    					successMessage(msg.config.saved);
	    				} else {
	    					errorMessage(serverResponse.message);
	    				}
	    		});
	    });
	    
	    // Bind configuration reset handler
	    $('#btn_reset').click(function() {
	    	closeMessage();
	    	loadConfiguration();
	    });
});

$.extend(
		// Default jqgrid options
		$.jgrid.defaults, { 
			datatype: "json", 
			height: 250,
			hidegrid: false,
			pgbuttons: false,
			pginput: false,
			viewrecords: false
		});


// Initialize document
function initializeDocument()
{			
    // Get Holmes version
    function getHolmesVersion() {
	    $.get('/backend/configuration/getVersion', function(response) {
	    	$("#version").html(msg.toolbar.version + " " + response);
	    });
    }
    getHolmesVersion();

    // Initialize video folders grid
	$("#list_video_folders").jqGrid({
		url:'/backend/configuration/getVideoFolders', 
		colNames:[msg.video.id,msg.video.label, msg.video.path], 
		colModel:[ {name:"id",index:"id", width:0 , hidden:true, editable:false, sortable: false}, 
		           {name:"label",index:"label", width:150, editable:true, editrules:{required: true}, sortable: false}, 
		           {name:"path",index:"path", width:450, editable:true, editrules:{required: true}, editoptions: {size:70}, sortable: false,
		        	   formoptions:{elmsuffix:'<a id="videoFoldersBrowse" href="javascript:void(0)" style="position:absolute"><span class="ui-icon ui-icon-folder-collapsed"></span></a>'}} 
		          ], 
		caption: msg.video.folders,
		pager: '#list_video_folders_nav',
		editurl:"/backend/configuration/editVideoFolder"
	});
	// Video folders navigation options
	$("#list_video_folders").jqGrid('navGrid','#list_video_folders_nav', 
			// global options
			{search: false, edittitle: msg.nav.edit, addtitle: msg.nav.add, deltitle:msg.nav.remove, 
				refreshtitle:msg.nav.refresh, alertcap:msg.alert, alerttext:msg.alertmsg}, 
			// edit options
			{height:150, width: 600, reloadAfterSubmit:true, closeOnEscape:true, closeAfterEdit:true, 
				editCaption: msg.video.edit.caption, bSubmit: msg.button.submit, bCancel: msg.button.cancel, 
				afterSubmit: function(response,postdata){ return getEditResponseData(response);},
			    beforeShowForm: function(form){$("#videoFoldersBrowse").unbind('click'); $("#videoFoldersBrowse").click(function(){ browseFolder(form.attr('id')); });}
		    }, 
			// add options
			{height:150, width: 600, reloadAfterSubmit:true, closeOnEscape:true, closeAfterAdd:true, 
				addCaption: msg.video.add.caption, bSubmit: msg.button.submit, bCancel: msg.button.cancel, 
				afterSubmit: function(response,postdata){ return getEditResponseData(response);},
			    beforeShowForm: function(form){$("#videoFoldersBrowse").unbind('click'); $("#videoFoldersBrowse").click(function(){ browseFolder(form.attr('id')); });}
		    },
			// delete options
			{reloadAfterSubmit:true, closeOnEscape:true, 
				caption: msg.video.remove.caption, msg: msg.video.remove.msg, bSubmit: msg.button.remove, bCancel: msg.button.cancel, 
				afterSubmit: function(response,postdata){ return getEditResponseData(response);}}
	);
	
	// Initialize audio folders grid
	$("#list_audio_folders").jqGrid({
		url:'/backend/configuration/getAudioFolders', 
		colNames:[msg.audio.id,msg.audio.label, msg.audio.path], 
		colModel:[ {name:"id",index:"id", width:0 , hidden:true, editable:false, sortable: false}, 
		           {name:"label",index:"label", width:150, editable:true, editrules:{required: true}, sortable: false}, 
		           {name:"path",index:"path", width:450, editable:true, editrules:{required: true}, editoptions: {size:70}, sortable: false, 
		        	   formoptions:{elmsuffix:'<a id="audioFoldersBrowse" href="javascript:void(0)" style="position:absolute"><span class="ui-icon ui-icon-folder-collapsed"></span></a>'}}
		         ], 
		caption: msg.audio.folders,
		pager: '#list_audio_folders_nav', 
		editurl:"/backend/configuration/editAudioFolder" 
	});
	// Audio folders navigation options
	$("#list_audio_folders").jqGrid('navGrid','#list_audio_folders_nav', 
			// global options
			{search: false, edittitle: msg.nav.edit, addtitle: msg.nav.add, deltitle:msg.nav.remove, 
				refreshtitle:msg.nav.refresh, alertcap:msg.alert, alerttext:msg.alertmsg},
			// edit options
			{height:150, width: 600, reloadAfterSubmit:true, closeOnEscape:true, closeAfterEdit:true, 
				editCaption: msg.audio.edit.caption, bSubmit: msg.button.submit, bCancel: msg.button.cancel, 
				afterSubmit: function(response,postdata){ return getEditResponseData(response);},
			    beforeShowForm: function(form){$("#audioFoldersBrowse").unbind('click'); $("#audioFoldersBrowse").click(function(){ browseFolder(form.attr('id')); });}
			},
			// add options
			{height:150, width: 600, reloadAfterSubmit:true, closeOnEscape:true, closeAfterAdd:true, 
				addCaption: msg.audio.add.caption, bSubmit: msg.button.submit, bCancel: msg.button.cancel, 
				afterSubmit: function(response,postdata){ return getEditResponseData(response);},
		    	beforeShowForm: function(form){$("#audioFoldersBrowse").unbind('click'); $("#audioFoldersBrowse").click(function(){ browseFolder(form.attr('id')); });}
			}, 
			// delete options
			{reloadAfterSubmit:true, closeOnEscape:true, 
				caption: msg.audio.remove.caption, msg: msg.audio.remove.msg, bSubmit: msg.button.remove, bCancel: msg.button.cancel, 
				afterSubmit: function(response,postdata){ return getEditResponseData(response);}} 
	);
	
	// Initialize picture folders grid
	$("#list_picture_folders").jqGrid({
		url:'/backend/configuration/getPictureFolders', 
		colNames:[msg.picture.id,msg.picture.label, msg.picture.path], 
		colModel:[ {name:"id",index:"id", width:0 , hidden:true, editable:false, sortable: false}, 
		           {name:"label",index:"label", width:150, editable:true, editrules:{required: true}, sortable: false}, 
		           {name:"path",index:"path", width:450, editable:true, editrules:{required: true}, editoptions: {size:70}, sortable: false,
		        	   formoptions:{elmsuffix:'<a id="pictureFoldersBrowse" href="javascript:void(0)" style="position:absolute"><span class="ui-icon ui-icon-folder-collapsed"></span></a>'}}
		          ], 
		caption: msg.picture.folders,
		pager: '#list_picture_folders_nav', 
		editurl:"/backend/configuration/editPictureFolder" 
	});
	// Picture folders navigation options
	$("#list_picture_folders").jqGrid('navGrid','#list_picture_folders_nav',
			// global options
			{search: false, edittitle: msg.nav.edit, addtitle: msg.nav.add, deltitle:msg.nav.remove, 
				refreshtitle:msg.nav.refresh, alertcap:msg.alert, alerttext:msg.alertmsg},
			// edit options
			{height:150, width: 600, reloadAfterSubmit:true, closeOnEscape:true, closeAfterEdit:true, 
				editCaption: msg.picture.edit.caption, bSubmit: msg.button.submit, bCancel: msg.button.cancel, 
				afterSubmit: function(response,postdata){ return getEditResponseData(response);},
			    beforeShowForm: function(form){$("#pictureFoldersBrowse").unbind('click'); $("#pictureFoldersBrowse").click(function(){ browseFolder(form.attr('id')); });}
			}, 
			// add options
			{height:150, width: 600, reloadAfterSubmit:true, closeOnEscape:true, closeAfterAdd:true, 
				addCaption: msg.picture.add.caption, bSubmit: msg.button.submit, bCancel: msg.button.cancel, 
				afterSubmit: function(response,postdata){ return getEditResponseData(response);},
				beforeShowForm: function(form){$("#pictureFoldersBrowse").unbind('click'); $("#pictureFoldersBrowse").click(function(){ browseFolder(form.attr('id')); });}
			},
			// delete options
			{reloadAfterSubmit:true, closeOnEscape:true, 
				caption: msg.picture.remove.caption, msg: msg.picture.remove.msg, bSubmit: msg.button.remove, bCancel: msg.button.cancel, 
				afterSubmit: function(response,postdata){ return getEditResponseData(response);}}
	);
	
	// Initialize pod-casts grid
	$("#list_podcasts").jqGrid({
		url:'/backend/configuration/getPodcasts', 
		colNames:[msg.podcast.id,msg.podcast.label, msg.podcast.url], 
		colModel:[ {name:"id",index:"id", width:0 , hidden:true, editable:false, sortable: false}, 
		           {name:"label",index:"label", width:150, editable:true, editrules:{required: true}, sortable: false}, 
		           {name:"path",index:"path", width:450, editable:true, editrules:{required: true}, editoptions: {size:70}, sortable: false} 
		          ], 
		caption: msg.podcast.folders,
		pager: '#list_podcasts_nav', 
		editurl:"/backend/configuration/editPodcast" 
	});
	// Pod-cast navigation options
	$("#list_podcasts").jqGrid('navGrid','#list_podcasts_nav', 
			// global options
			{search: false, edittitle: msg.nav.edit, addtitle: msg.nav.add, deltitle:msg.nav.remove, 
				refreshtitle:msg.nav.refresh, alertcap:msg.alert, alerttext:msg.alertmsg}, 
			// edit options
			{height:150, width: 600, reloadAfterSubmit:true, closeOnEscape:true, closeAfterEdit:true, 
				editCaption: msg.podcast.edit.caption, bSubmit: msg.button.submit, bCancel: msg.button.cancel, 
				afterSubmit: function(response,postdata){ return getEditResponseData(response);}},
			// add options
			{height:150, width: 600, reloadAfterSubmit:true, closeOnEscape:true, closeAfterAdd:true, 
				addCaption: msg.podcast.add.caption, bSubmit: msg.button.submit, bCancel: msg.button.cancel, 
				afterSubmit: function(response,postdata){ return getEditResponseData(response);}},
			// delete options
			{reloadAfterSubmit:true, closeOnEscape:true, 
				caption: msg.podcast.remove.caption, msg: msg.podcast.remove.msg, bSubmit: msg.button.remove, bCancel: msg.button.cancel, 
				afterSubmit: function(response,postdata){ return getEditResponseData(response);}} 
	);
}

// Callback for edit/add/delete item 
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

// Load configuration data
function loadConfiguration() {
    $.getJSON('/backend/configuration/getConfiguration', function(response) {
    		$("#text_server_name").val(response.serverName);
    		$("#text_http_server_port").val(response.httpServerPort);
    });
}

// Show folder browser dialog
function browseFolder(formId) {
	//alert(formId);
	alert('Work in progress');
}

// Show configuration success message
function successMessage(message) {
	$("#message").html("<span>" + message + "</span><a id='close_message' href='javascript:void(0)' style='float:right' class='ui-icon ui-icon-close'></a>");
	$('#close_message').click(function() { closeMessage(); });
}

// Show configuration error message
function errorMessage(message) {
	$("#message").html("<span class='ui-state-error-text'>" + message + "</span><a id='close_message' href='javascript:void(0)' style='float:right' class='ui-icon ui-icon-close'></a>");
	$('#close_message').click(function() { closeMessage(); });
}

// Close succes/error message
function closeMessage() {
	$('#close_message').unbind();
	$("#message").html("");
}
