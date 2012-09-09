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
	$("#tabs").tabs();

	// Initialize i18n
	$.i18n.properties({
	    name:'messages', 
	    path:'bundle/',
	    mode:'both',
	    callback: function() {
	    	// Internationalize i18n elements
	    	$(".i18n").each( function(i,elem) {
	    		if ($(elem).data('msg') != undefined)
	    			$(elem).html(($.i18n.prop($(elem).data('msg'))));
	    	});
	    	// Initialize document data once i18n is loaded
    		initializeDocumentData();
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
    
    $(".hover").hover(function() {
    		$(this).addClass("ui-state-hover");
		},function(){
			$(this).removeClass("ui-state-hover");
	});
    
    $('#easter').click(function() {
    	$('body').attr('class','roll');
    });
    
    // Bind configuration submit handler
    $('#btn_submit').click(function() {
    	$.post('/backend/configuration/editConfiguration',
    		{serverName : $("#text_server_name").val(),
    			httpServerPort : $("#text_http_server_port").val(),
    			prependPodcastItem : $("#chk_prependPodcastItem").is(':checked') ? "true":"false"
    		},
    		function(response) {
    			if (response.status){
    				successMessage(msg.config.saved);
    			} else {
    				errorMessage(response.message);
    			}
    		});
    });
    
    // Bind configuration reset handler
    $('#btn_reset').click(function() {
    	closeMessage();
    	getConfiguration();
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
	}
);

// Initialize document data
function initializeDocumentData() {			
    // Get Holmes version
    getHolmesVersion();

    // Get configuration data
    getConfiguration();
    
    // Initialize folder tree dialog
    initializeFolderTreeDlg();
    
    navGlobalOptions = {search: false, edittitle: msg.nav.edit, addtitle: msg.nav.add, deltitle:msg.nav.remove, 
						refreshtitle:msg.nav.refresh, alertcap:msg.alert, alerttext:msg.alertmsg };
    
    // Initialize video folders grid
    var editVideoOptions = {height:150, width: 600, reloadAfterSubmit:true, closeOnEscape:true, closeAfterEdit:true, modal:true,
			editCaption: msg.video.edit.caption, bSubmit: msg.button.submit, bCancel: msg.button.cancel, 
			afterSubmit: function(response,postdata){ return getEditResponseData(response);},
		    beforeShowForm: function(form){$("#videoFoldersBrowse").unbind('click'); $("#videoFoldersBrowse").click(function(){ browseFolder(form.attr('id')); });},
			onClose: function() {folderDialog.dialog('close');}
	    };
    
	$("#list_video_folders").jqGrid({
		url:'/backend/configuration/getVideoFolders', 
		colNames:[msg.video.id,msg.video.label, msg.video.path], 
		colModel:[ {name:"id",index:"id", width:0 , hidden:true, editable:false, sortable: false}, 
		           {name:"label",index:"label", width:170, editable:true, editrules:{required: true}, editoptions: {size:30}, sortable: false}, 
		           {name:"path",index:"path", width:570, editable:true, editrules:{required: true}, editoptions: {size:70}, sortable: false,
		        	   formoptions:{elmsuffix:'<a id="videoFoldersBrowse" href="javascript:void(0)" style="position:absolute"><span class="ui-icon ui-icon-folder-collapsed"></span></a>'}} 
		          ], 
		caption: msg.video.folders,
		pager: '#list_video_folders_nav',
		editurl:"/backend/configuration/editVideoFolder",
		ondblClickRow: function(id){$(this).editGridRow(id,editVideoOptions);}
	});
	// Video folders navigation options
	$("#list_video_folders").jqGrid('navGrid','#list_video_folders_nav', 
			// global options
			navGlobalOptions,
			// edit options
			editVideoOptions,
			// add options
			{height:150, width: 600, reloadAfterSubmit:true, closeOnEscape:true, closeAfterAdd:true,  modal:true,
				addCaption: msg.video.add.caption, bSubmit: msg.button.submit, bCancel: msg.button.cancel, 
				afterSubmit: function(response,postdata){ return getEditResponseData(response);},
			    beforeShowForm: function(form){$("#videoFoldersBrowse").unbind('click'); $("#videoFoldersBrowse").click(function(){ browseFolder(form.attr('id')); });},
				onClose: function() {folderDialog.dialog('close');}
		    },
			// delete options
			{reloadAfterSubmit:true, closeOnEscape:true, 
				caption: msg.video.remove.caption, msg: msg.video.remove.msg, bSubmit: msg.button.remove, bCancel: msg.button.cancel, 
				afterSubmit: function(response,postdata){ return getEditResponseData(response);}}
	);
	
	// Initialize audio folders grid
	var editAudioOptions = {height:150, width: 600, reloadAfterSubmit:true, closeOnEscape:true, closeAfterEdit:true,  modal:true,
			editCaption: msg.audio.edit.caption, bSubmit: msg.button.submit, bCancel: msg.button.cancel, 
			afterSubmit: function(response,postdata){ return getEditResponseData(response);},
		    beforeShowForm: function(form){$("#audioFoldersBrowse").unbind('click'); $("#audioFoldersBrowse").click(function(){ browseFolder(form.attr('id')); });},
			onClose: function() {folderDialog.dialog('close');}
		};
	
	$("#list_audio_folders").jqGrid({
		url:'/backend/configuration/getAudioFolders', 
		colNames:[msg.audio.id,msg.audio.label, msg.audio.path], 
		colModel:[ {name:"id",index:"id", width:0 , hidden:true, editable:false, sortable: false}, 
		           {name:"label",index:"label", width:170, editable:true, editrules:{required: true}, editoptions: {size:30}, sortable: false}, 
		           {name:"path",index:"path", width:570, editable:true, editrules:{required: true}, editoptions: {size:70}, sortable: false, 
		        	   formoptions:{elmsuffix:'<a id="audioFoldersBrowse" href="javascript:void(0)" style="position:absolute"><span class="ui-icon ui-icon-folder-collapsed"></span></a>'}}
		         ], 
		caption: msg.audio.folders,
		pager: '#list_audio_folders_nav', 
		editurl:"/backend/configuration/editAudioFolder",
		ondblClickRow: function(id){$(this).editGridRow(id,editAudioOptions);}
	});
	// Audio folders navigation options
	$("#list_audio_folders").jqGrid('navGrid','#list_audio_folders_nav', 
			// global options
			navGlobalOptions,
			// edit options
			editAudioOptions,
			// add options
			{height:150, width: 600, reloadAfterSubmit:true, closeOnEscape:true, closeAfterAdd:true,  modal:true,
				addCaption: msg.audio.add.caption, bSubmit: msg.button.submit, bCancel: msg.button.cancel, 
				afterSubmit: function(response,postdata){ return getEditResponseData(response);},
		    	beforeShowForm: function(form){$("#audioFoldersBrowse").unbind('click'); $("#audioFoldersBrowse").click(function(){ browseFolder(form.attr('id')); });},
				onClose: function() {folderDialog.dialog('close');}
			}, 
			// delete options
			{reloadAfterSubmit:true, closeOnEscape:true, 
				caption: msg.audio.remove.caption, msg: msg.audio.remove.msg, bSubmit: msg.button.remove, bCancel: msg.button.cancel, 
				afterSubmit: function(response,postdata){ return getEditResponseData(response);}} 
	);
	
	// Initialize picture folders grid
	var editPictureOptions = {height:150, width: 600, reloadAfterSubmit:true, closeOnEscape:true, closeAfterEdit:true,  modal:true,
			editCaption: msg.picture.edit.caption, bSubmit: msg.button.submit, bCancel: msg.button.cancel, 
			afterSubmit: function(response,postdata){ return getEditResponseData(response);},
		    beforeShowForm: function(form){$("#pictureFoldersBrowse").unbind('click'); $("#pictureFoldersBrowse").click(function(){ browseFolder(form.attr('id')); });},
			onClose: function() {folderDialog.dialog('close');}
		};
	$("#list_picture_folders").jqGrid({
		url:'/backend/configuration/getPictureFolders', 
		colNames:[msg.picture.id,msg.picture.label, msg.picture.path], 
		colModel:[ {name:"id",index:"id", width:0 , hidden:true, editable:false, sortable: false}, 
		           {name:"label",index:"label", width:170, editable:true, editrules:{required: true}, editoptions: {size:30}, sortable: false}, 
		           {name:"path",index:"path", width:570, editable:true, editrules:{required: true}, editoptions: {size:70}, sortable: false,
		        	   formoptions:{elmsuffix:'<a id="pictureFoldersBrowse" href="javascript:void(0)" style="position:absolute"><span class="ui-icon ui-icon-folder-collapsed"></span></a>'}}
		          ], 
		caption: msg.picture.folders,
		pager: '#list_picture_folders_nav', 
		editurl:"/backend/configuration/editPictureFolder",
		ondblClickRow: function(id){$(this).editGridRow(id,editPictureOptions);}
	});
	// Picture folders navigation options
	$("#list_picture_folders").jqGrid('navGrid','#list_picture_folders_nav',
			// global options
			navGlobalOptions,
			// edit options
			editPictureOptions,
			// add options
			{height:150, width: 600, reloadAfterSubmit:true, closeOnEscape:true, closeAfterAdd:true,  modal:true,
				addCaption: msg.picture.add.caption, bSubmit: msg.button.submit, bCancel: msg.button.cancel, 
				afterSubmit: function(response,postdata){ return getEditResponseData(response);},
				beforeShowForm: function(form){$("#pictureFoldersBrowse").unbind('click'); $("#pictureFoldersBrowse").click(function(){ browseFolder(form.attr('id')); });},
				onClose: function() {folderDialog.dialog('close');}				
			},
			// delete options
			{reloadAfterSubmit:true, closeOnEscape:true, 
				caption: msg.picture.remove.caption, msg: msg.picture.remove.msg, bSubmit: msg.button.remove, bCancel: msg.button.cancel, 
				afterSubmit: function(response,postdata){ return getEditResponseData(response);}}
	);
	
	// Initialize pod-casts grid
	var editPodcastOptions = {height:150, width: 600, reloadAfterSubmit:true, closeOnEscape:true, closeAfterEdit:true,  modal:true,
			editCaption: msg.podcast.edit.caption, bSubmit: msg.button.submit, bCancel: msg.button.cancel, 
			afterSubmit: function(response,postdata){ return getEditResponseData(response);}
		};
	$("#list_podcasts").jqGrid({
		url:'/backend/configuration/getPodcasts', 
		colNames:[msg.podcast.id,msg.podcast.label, msg.podcast.url], 
		colModel:[ {name:"id",index:"id", width:0 , hidden:true, editable:false, sortable: false}, 
		           {name:"label",index:"label", width:220, editable:true, editrules:{required: true}, editoptions: {size:30}, sortable: false}, 
		           {name:"path",index:"path", width:520, editable:true, editrules:{required: true}, editoptions: {size:70}, sortable: false} 
		          ], 
		caption: msg.podcast.folders,
		pager: '#list_podcasts_nav', 
		editurl:"/backend/configuration/editPodcast",
		ondblClickRow: function(id){$(this).editGridRow(id,editPodcastOptions);}
	});
	// Pod-cast navigation options
	$("#list_podcasts").jqGrid('navGrid','#list_podcasts_nav', 
			// global options
			navGlobalOptions,
			// edit options
			editPodcastOptions,
			// add options
			{height:150, width: 600, reloadAfterSubmit:true, closeOnEscape:true, closeAfterAdd:true,  modal:true,
				addCaption: msg.podcast.add.caption, bSubmit: msg.button.submit, bCancel: msg.button.cancel, 
				afterSubmit: function(response,postdata){ return getEditResponseData(response);}
			},
			// delete options
			{reloadAfterSubmit:true, closeOnEscape:true, 
				caption: msg.podcast.remove.caption, msg: msg.podcast.remove.msg, bSubmit: msg.button.remove, bCancel: msg.button.cancel, 
				afterSubmit: function(response,postdata){ return getEditResponseData(response);}
			} 
	);
}

// Get Holmes version
function getHolmesVersion() {
    $.get('/backend/util/getVersion', function(response) {
    	$("#version").html(msg.toolbar.version + " " + response);
    });
}

// Get configuration data
function getConfiguration() {
    $.getJSON('/backend/configuration/getConfiguration', function(response) {
    	$("#text_server_name").val(response.serverName);
    	$("#text_http_server_port").val(response.httpServerPort);
    	$("#chk_prependPodcastItem").attr('checked', response.prependPodcastItem);
    });
}

// Folder dialog variables
var folderDialog;
var folderFormId;
var selectedFolder;
// Initialize folder tree dialog
function initializeFolderTreeDlg() {
	var folderTree = $("#folderTree").jstree({ 
		"json_data" : {
			"ajax" : {
				"url" : "/backend/util/getChildFolders",
				"type" : "POST",
				"data" : function (n) {
					return { "path" : n.data ? n.data("path") : "none" };
				}
			}
		},
		"themes" : {"dots" : false},
		"plugins" : [ "themes", "json_data", "ui" ]
	});
	folderTree.bind("select_node.jstree", function (e, data) {selectedFolder = data.rslt.obj.data("path"); });
	folderTree.bind("dblclick.jstree", function (event) {folderTreeDialogOk();});
	folderDialog = $("#folderTree").dialog({ 
		autoOpen : false , 
		title : msg.treeFolder.dialog.title , 
		height : 300 ,  
		buttons: [{ text : msg.treeFolder.dialog.ok , click : folderTreeDialogOk}]
	});
}

// Callback for folder tree Ok button
function folderTreeDialogOk() {
	folderDialog.dialog('close');
	$('#'+folderFormId).find('#path').each(
		function() {$(this).val(selectedFolder);}
	);
}

// Show folder browser dialog
function browseFolder(formId) {
	folderFormId = formId;
	folderDialog.dialog('open');
}

// Callback for edit/add/delete grid item 
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

// Show configuration success message
function successMessage(message) {
	$("#message").html("<div class='ui-state-highlight'>" + message + "<a id='close_message' href='javascript:void(0)' style='float:right' class='ui-icon ui-icon-close'></a></div>");
	$('#close_message').click(function() { closeMessage(); });
}

// Show configuration error message
function errorMessage(message) {
	$("#message").html("<div class='ui-state-error'>" + message + "<a id='close_message' href='javascript:void(0)' style='float:right' class='ui-icon ui-icon-close'></a></div>");
	$('#close_message').click(function() { closeMessage(); });
}

// Close succes/error message
function closeMessage() {
	$('#close_message').unbind();
	$("#message").html("");
}
