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
	// Public
	return {
	    "init": function(url, title, okLabel) {
	    	//TODO add folderTree div to dom
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
	    	_folderTree.bind("select_node.jstree", function (e, data) {_selectedFolder = data.rslt.obj.data("path"); });
	    	_folderTree.bind("dblclick.jstree", function (event) {_folderTreeDialogOnOk();});
	    	_folderDialog = $("#folderTree").dialog({
	    		modal : true ,
	    		autoOpen : false , 
	    		title : title , 
	    		height : 300 ,  
	    		buttons: [{ text : okLabel , click : _folderTreeDialogOnOk}]
	    	});
	    	console.log('init folder tree');
	    },
	    "show" : function(linkedItem) {
	    	_linkedItem = linkedItem;
	    	_selectedFolder = '';
	    	_folderDialog.dialog('open');
	    }
	};
})();