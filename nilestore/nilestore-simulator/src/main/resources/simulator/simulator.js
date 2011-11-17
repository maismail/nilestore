var currentState = "";
var currentSelectedNodeId = "";
var NC = 5;
var K;
var N;
var SC;
var overlay;
var loggerTimerId;
var nodesState = new Array();

function init() {
	var myLayout = $('body').layout({
		north__spacing_closed : 20,
		north__resizable : false,
		north__size : 60,
		west__size : 220,
		east__size : 250,
		east__spacing_closed : 20,
		west__spacing_closed : 20
	});

	initToolbar();
	initPanes();
	initFileUpload();
	initDialog();
	initNodeController();
}

function initToolbar() {
	$("#nstoolbar").css({
		"text-align" : "center"
	});

	$("#createNetwork").button();
	$("#runNetwork").button();
	$("#stopNetwork").button();
	$("#nodetype").buttonset();

	$("#createNetwork").click(function() {
		// create the dialog and get parameters for the creation
		$("#createDialog").dialog("open");
	});

	$("#runNetwork").click(function() {
		runNodes();
	});

	$("#stopNetwork").click(function() {
		alert("stop");
		stopNodes();
	});
}

function initPanes() {

	$("#nodePaneTab").tabs({
		select : function(event, ui) {
			var i = ui.index;
			nodesTabs(i);
			return true;
		}
	});

	$("#myStoreDiv").css({
		"text-align" : "center"
	});

	$("#nodePaneTab").tabs("select", 0);
	$("#monitorPane").tabs();
	$("#monitorPane").tabs("select", 0);

	$("#nodesRadio").click(function(event) {
		changeCurrentView("Nodes");
	});

	$("#monitorRadio").click(function(event) {
		changeCurrentView("Monitor");
		initCurrView("dest=0", "currentView");
		initGrView("dest=0", "groupedView");
	});

	$("#introducerRadio").click(function(event) {
		changeCurrentView("Introducer");
	});

	hideAllPanes();
}

function initNodeController() {
	$("#controlNode").buttonset();
	$("#controlNodeParent").css({
		"text-align" : "center"
	});

	$("#nodeOn").click(function(event) {
		alert("on");
		contNode("start");
	});

	$("#nodeOff").click(function(event) {
		alert("off");
		contNode("stop");
	});
}

function initDialog() {

	$("#ovelaytype").buttonset();
	$("#createDialog").css({
		"text-align" : "center"
	});
	$("#createDialog").dialog({
		autoOpen : false,
		height : 270,
		width : 350,
		resizable : false,
		modal : true,
		buttons : {
			"Create" : function() {

				if (currentState == "") {

					currentState = "create";
					NC = parseInt($("#totalnodes").val());
					SC = parseInt($("#storagenodes").val());
					K = parseInt($("#k").val());
					N = parseInt($("#n").val());
					createNodes();
				}
				$(this).dialog("close");
			}
		},
		close : function() {

		}
	});
}

function getLog() {
	$.ajax({
		type : "POST",
		url : "/getlog",
		data : "mdest=" + currentSelectedNodeId,
		dataType : "json",
		success : function(data) {
			// data = jQuery.parseJSON(res);
			for ( var i = 0; i < data.length; i++) {
				var logline = data[i];
				var p = $(document.createElement("p"));
				if (logline.search("INFO") != -1) {
					p.text(logline).css({
						"color" : "#0073ea"
					});
				}
				$("#logger").append(p);
			}
			var parent = $("#logger").parent();
			parent.scrollTo('100%', 50, {
				axis : "y"
			});
			// alert(parent.height()+ " and "+ parent.scrollTop());
			// parent.scrollTop(parent.height()+200);
			loggerTimerId = setTimeout("getLog();", 1000);
		}
	});
}


function createNodes() {
	var container = $("#nodesWestDiv");
	for (i = 1; i < NC + 1; i++) {
		nodesState[i] = 0;
		var id = 'node' + i;
		var item = id;

		var c = false;
		if (i == 1) {
			c = true;
		}

		var css = {
			"color" : "black",
			"width" : "100"
		};

		if (i <= SC) {
			css = {
				"width" : "100"
			};
		}

		var inputElem = $('<input>').attr({
			type : 'radio',
			id : id,
			name : 'radio',
			checked : c
		}).click(
				function(event) {
					var radio = $(this)[0];
					currentSelectedNodeId = getDestId(radio.id);
					changeCurrentView("Nodes");
					$("#logger").html("");
				});

		container.append(inputElem).append($('<label>').attr({
			'for' : id
		}).text(item).css(css)).append($('<br>'));

		/*$("#" + id).button({
			icons : {
				primary : 'ui-icon-circle-close'
			}
		});*/
	}
	container.buttonset();
	changeNodesStateIcon("stop");
	container.show();
}

function runNodes() {
	if (currentState == "running") {
		alert("already running");
		return;
	}
	$.ajax({
		type : "POST",
		url : "/run",
		data : {
			"nc" : NC,
			"sc" : SC,
			"k" : K,
			"n" : N
		},
		dataType : "text",
		success : function(res) {
			alert(res);
			currentState = "running";
			changeNodesStateIcon("start");
			changeCurrentView("Nodes");
			getLog();
		}
	});

}

function contNode(cont) {
	$.ajax({
		type : "POST",
		url : "/" + cont,
		data : {
			"mdest" : currentSelectedNodeId
		},
		dataType : "text",
		success : function(res) {
			alert(res);
			changeCurrentView("Nodes");
			if (cont == "start") {
				nodesState[currentSelectedNodeId] = 1;
				$("#node" + currentSelectedNodeId).button("option","icons",{
					primary : 'ui-icon-circle-check'
				});
				$("#nodePaneTab").show();
			} else {
				nodesState[currentSelectedNodeId] = 0;
				$("#node" + currentSelectedNodeId).button("option","icons",{
					primary : 'ui-icon-circle-close'
				});
				$("#nodePaneTab").hide();
			}
		}
	});
}

function stopNodes() {
	if (currentState != "running") {
		alert("already not running");
		return;
	}

	$.ajax({
		type : "POST",
		url : "/stop",
		dataType : "text",
		success : function(res) {
			alert(res);
			currentState = "";
			clearTimeout(loggerTimerId);
			hideAllPanes();
			changeNodesStateIcon("stop");
		}
	});
}

function changeNodesStateIcon(cont) {
	var icon = "close";
	var n=0;
	if (cont == "start") {
		icon = "check";
		n=1;
	}
	for (i = 1; i < NC + 1; i++) {
		nodesState[i]=n;
		$("#node" + i).button("option","icons",{
			primary : 'ui-icon-circle-' + icon
		});
	}
}

function initFileUpload() {
	$('#file_upload')
			.fileUploadUI(
					{
						uploadTable : $('#files'),
						buildUploadRow : function(files, index) {
							var file = files[index];
							return $('<tr>'
									+ '<td class="file_upload_start">'
									+ '<div class="ui-state-default ui-corner-all" title="Start Upload">'
									+ '<span class="ui-icon ui-icon-circle-arrow-n">Start Upload<\/span>'
									+ '<\/div>'
									+ '<\/td>'
									+ '<td>'
									+ file.name
									+ '<\/td>'
									+ '<td class="file_upload_progress"><div><\/div><\/td>'
									+ '<td class="file_upload_cancel">'
									+ '<button class="ui-state-default ui-corner-all" title="Cancel">'
									+ '<span class="ui-icon ui-icon-cancel">Cancel<\/span>'
									+ '<\/button>' + '<\/td>' + '<\/tr>');
						},
						beforeSend : function(event, files, index, xhr,
								handler, callBack) {
							handler.uploadRow.find('.file_upload_start').click(
									function() {
										callBack();
									});
						},
						onLoad : function(event, files, index, xhr, handler) {
							/*
							 * var json; if (typeof xhr.responseText !== undef) {
							 * json = $.parseJSON(xhr.responseText); } else { //
							 * Instead of an XHR object, an iframe is used for
							 * legacy browsers: json =
							 * $.parseJSON(xhr.contents().text()); }
							 */
							// handler.uploadRow.remove();
							handler.removeNode(handler.uploadRow);
							// alert(xhr.responseText);
							$("#uploadResults").html(xhr.responseText);
						},
						onAbort : function(event, files, index, xhr, handler) {
							handler.removeNode(handler.uploadRow);
							alert("aborted");
						}
					});

}

function mystore() {
	//alert("mystore for " + currentSelectedNodeId);
	$.ajax({
		type : "POST",
		url : "/mystore",
		dataType : "text",
		data : "dest=" + currentSelectedNodeId,
		success : function(res) {
			//alert(res);
			$("#myStoreDiv").html(res);
		}
	});
}

function explore() {
	//alert("storage for " + currentSelectedNodeId);
	$.ajax({
		type : "POST",
		url : "/explore",
		data : "dest=" + currentSelectedNodeId,
		dataType : "json",
		success : function(res) {
			//alert(res);
			SI = res.SI;
			SIZES = res.sizes;
			subSI = res.subsi;

			var stab = $("#storageTable");
			$("#storageTablebody").html("");
			for ( var i = 0; i < SI.length; i++) {
				var sid = "node" + i;
				var row = $("<tr>").attr({
					id : sid
				});

				$("<td>").text(SI[i]).appendTo(row);
				$("<td>").text(SIZES[i]).appendTo(row);
				$("<td>").text("----").appendTo(row);

				row.appendTo(stab);

				var mySub = subSI[i];
				var pieceSize = SIZES[i] / mySub.length;
				for ( var k = 0; k < mySub.length; k++) {
					var row = $("<tr>").addClass("child-of-" + sid);

					row.append($("<td>").text(mySub[k]));
					row.append($("<td>").text(pieceSize));

					var buttonId = "dump." + SI[i] + "." + mySub[k];
					// var buttonId = "dump"+i+k;
					var button = $("<button>").attr({
						id : buttonId
					}).text("dump").click(function() {
						var id = $(this)[0].id;
						alert(id);
					});
					$("#" + buttonId).button();

					row.append($("<td>").append(button));

					row.appendTo(stab);
				}
			}

			stab.treeTable();
			// alert(SI);
			// alert(SIZES);

		}
	});
}

function nodesTabs(i) {
	// alert(i);
	// UploadTab
	if (i == 0) {
		$("#destId").val(currentSelectedNodeId);
	}
	// myStoreTab
	else if (i == 1) {
		mystore();
	}
	// StorageTab
	else if (i == 2) {
		explore();
	}
}

function showNodePane() {
	
	if (currentSelectedNodeId == 0 || currentSelectedNodeId == -1) {
		currentSelectedNodeId = 1;
		// $('input[name=radio]:eq(0)').attr('checked', 'checked');
		$('input[name=radio]:checked + label').removeClass("ui-state-active");
		$('input[name=radio]:eq(0) + label').addClass("ui-state-active");
		$("#node1").checked = true;
	}
	
	$('input[name=controlNode] + label').removeClass("ui-state-active");
	// currrent node is on	
	if (nodesState[currentSelectedNodeId] == 1) {
		$('input[name=controlNode]:eq(0) + label').addClass("ui-state-active");
		$("#nodeOn").checked = true;
		$("#nodeOff").checked = false;
		$("#nodePaneTab").show();
	// current node is off
	} else {
		$('input[name=controlNode]:eq(1) + label').addClass("ui-state-active");
		$("#nodeOff").checked = true;
		$("#nodeOn").checked = false;
		$("#nodePaneTab").hide();
		return;
	}
	
	if (currentSelectedNodeId > SC) {
		$("#storageTabA").hide();
		$("#storageTab").hide();
	} else {
		$("#storageTabA").show();
		$("#storageTab").show();
	}
	$("#nodePane").show();
	$("#uploadResults").html("");
	$("#nodesWestDiv").show();

	var selected = $("#nodePaneTab").tabs("option", "selected");
	nodesTabs(selected);
}

function showMonitorPane() {
	currentSelectedNodeId = 0;
	$("#monitorPane").show();
	$("#monitorWestDiv").show();
}

function showIntroducerPane() {
	currentSelectedNodeId = -1;
	$("#introducerPane").show();
	$("#introducerWestDiv").show();

}

function changeCurrentView(header) {
	if (header == "Nodes") {
		checkState(showNodePane);
		$("#monitorPane").hide();
		$("#introducerPane").hide();

		$("#monitorWestDiv").hide();
		$("#introducerWestDiv").hide();
	} else if (header == "Monitor") {
		checkState(showMonitorPane);
		$("#nodePane").hide();
		$("#introducerPane").hide();

		$("#nodesWestDiv").hide();
		$("#introducerWestDiv").hide();
	} else if (header == "Introducer") {
		checkState(showIntroducerPane);
		$("#monitorPane").hide();
		$("#nodePane").hide();

		$("#monitorWestDiv").hide();
		$("#nodesWestDiv").hide();
	}
	showCurrentNode();
	$("#logger").html("");
}

function checkState(callback) {
	if (currentState == "running") {
		callback();
	} else {
		alert("you should run the grid to interact with nodes");
	}
}

function hideAllPanes() {
	$("#nodePane").hide();
	$("#monitorPane").hide();
	$("#introducerPane").hide();
}

function showCurrentNode() {
	var nodename;
	if (currentSelectedNodeId == 0) {
		nodename = "Monitor";
	} else if (currentSelectedNodeId == -1) {
		nodename = "Introducer";
	} else {
		nodename = "node" + currentSelectedNodeId;
	}

	$("#currentNode").html("<b>Current Selected Node:</b>  " + nodename);
}

function getDestId(nodeid) {
	return nodeid.substr(4);
}