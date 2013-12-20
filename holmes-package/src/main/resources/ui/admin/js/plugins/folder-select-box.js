/**
 * Select local folder dialog box
 */
var folderSelectBox = (function() {
    var _selectedFolder = '',
    _linkedItem,
    _folderTree,
    _folderDialog;

    // Private
    function _folderTreeDialogOnOk() {
    	_folderDialog.dialog('close');
    	_linkedItem.val(_selectedFolder);
    }
    
    function _folderTreeDialogOnCancel() {
    	_folderDialog.dialog('close');
    }    
	// Public
	return {
	    init: function(url, title, cancelLabel, okLabel) {
	    	// add div to body
	    	$('body').append('<div id="folderTree"></div>');

	    	// build folder tree
	    	_folderTree = $("#folderTree").jstree({ 
	    		"json_data" : {
	    			"ajax" : {
	    				"url" : url,
	    				"type" : "POST",
	    				"data" : function (n) {
	    					return { "path" : n.data ? n.data("path") : "none" };
	    				}
	    			}
	    		},
	    		"themes" : {"dots" : false},
	    		"plugins" : [ "themes", "json_data", "ui" ]
	    	});
	    	// bind folder tree events
	    	_folderTree.bind("select_node.jstree", function (e, data) {_selectedFolder = data.rslt.obj.data("path"); });
	    	_folderTree.bind("dblclick.jstree", function (event) {_folderTreeDialogOnOk();});
	    	
	    	// build folder dialog
	    	_folderDialog = $("#folderTree").dialog({
	    		modal : false ,
	    		className : 'modal-dialog',
	    		autoOpen : false ,
	    		closeButton: true,
	    		title : title ,
	    		height : 300 ,
	    		buttons: {
                    cancel: {
                        text : cancelLabel,
                        class: "btn btn-default",
                        click: _folderTreeDialogOnCancel
                    },
                    success: {
                        text: okLabel,
                        class: "btn btn-primary",
                        click: _folderTreeDialogOnOk
                    }
	    		}
	    	});
	    	
	    	// dialog style hack
	    	$('.ui-dialog-titlebar-close').html('x');
	    },
	    show : function(linkedItem) {
	    	// show dialog
	    	_linkedItem = linkedItem;
	    	_selectedFolder = '';
	    	$("#folderTree").jstree("deselect_all");
	    	_folderDialog.dialog('open');
	    },
	    hide : function() {
	    	// hide dialog
	    	_folderDialog.dialog('close');
	    }
	};
})();