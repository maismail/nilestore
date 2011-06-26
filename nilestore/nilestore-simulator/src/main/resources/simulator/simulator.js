var currentState="";
var currentSelectedNodeId="";
var NC =5;
var K;
var N;
var SC;
var overlay;

function init()
{
	var myLayout = $('body').layout({				
				 north__spacing_closed:	20
				,north__resizable : false
				,north__size : 60
				,west__size: 120
				,east__size: 250
				,east__spacing_closed:	20
				,west__spacing_closed:	20
			});
			
			$("#nstoolbar").css({"text-align":"center"});
			$("#createNetwork").button();
			$("#runNetwork").button();
			$("#nodetype").buttonset();
			
			$("#nodePane").tabs({
					select: function(event, ui) {
					var i = ui.index;
					nodesTabs(i);
					return true;
    			}
			});
			
			$("#nodePane").tabs( "select" ,0);	
			
			$("#monitorPane").tabs();
			$("#monitorPane").tabs( "select" ,0);		
			
			initPanes();
			/*$("#nodesAccordion").accordion({ fillSpace: true });
			$("#nodesAccordion").bind("accordionchange", function(event,ui)
			{
				var h = ui.newHeader.text();
				changeCurrentView(h);
			});*/
			
			$("#createNetwork").click(function(){		
				//create the dialog and get parameters for the creation
				$( "#createDialog" ).dialog( "open" );	
				//createNodes(nodesCount);	
			});
			
			$("#runNetwork").click(function(){
				runNodes();
			});
			
			initFileUpload();

			$("#myStoreDiv").css({"text-align":"center"});
			//$("#nodesAccordion").hide();
			initDialog();
}

function initPanes(){
	$("#nodesRadio").click( function(event){
		changeCurrentView("Nodes");
	});
	
	$("#monitorRadio").click( function(event){
		changeCurrentView("Monitor");
		initCurrView("dest=0","currentView");
		initGrView("dest=0","groupedView");
	});
	
	$("#introducerRadio").click( function(event){
		changeCurrentView("Introducer");
	});
	$("#nodePane").hide();
	$("#monitorPane").hide();
	$("#introducerPane").hide();		
}

function initDialog(){

$("#ovelaytype").buttonset();
$("#createDialog").css({"text-align" : "center"});
$("#createDialog").dialog({
			autoOpen: false,
			height: 270,
			width: 350,
			resizable: false,
			modal: true,
			buttons: {
				"Create": function() {
						
						if(currentState == ""){
						
							currentState = "create";
							NC=parseInt($("#totalnodes").val());
							SC=parseInt($("#storagenodes").val());
							K=parseInt($("#k").val());			
							N=parseInt($("#n").val());	
							createNodes();
						}
					$(this).dialog("close");
				}
			},
			close: function() {
			
			}
		});
}
function getLog()
{
	$.ajax({
		type:"POST",
		url: "/getlog",
		data:"mdest="+currentSelectedNodeId,
  		success: function(res){
  						data = jQuery.parseJSON(res);
  						for(var i=0;i<data.length;i++){
  								var logline = data[i];
  								var p = $(document.createElement("p"));
  								if(logline.search("INFO") != -1){
									 p.text(logline).css({"color" : "#0073ea"});
  								}
  								$("#logger").append(p);
						}
						var parent = $("#logger").parent();
						parent.scrollTo('100%',50,{axis:"y"});
						//alert(parent.height()+ " and "+ parent.scrollTop());
						//parent.scrollTop(parent.height()+200);
						//test();
						setTimeout("getLog();",1000);
  					}
  		});
}

//TODO: add on/off for each node
function createNodes()
{
	var container = $("#nodesWestDiv");
	
	for(i=1;i<NC+1;i++)
	{
		var id = 'node'+i;
		var item = id;
		if(i < 10){
			item+='..';
		}
		
		var c = false;
		if(i == 1){
			c = true;
		}
		
		var css={"color" : "black"};
		if(i <= SC){
				css={};
		}
		
		 container.append(
                $(document.createElement("input")).attr({
                         type:  'radio'                         
                        ,id:    id
                        ,name:  'radio'
                        ,checked : c
                })
                .click( function( event )
                {
                        var radio = $(this)[0];
                        //alert( radio.value );
                        //currentSelectedNode=radio.id;
                        //nodeid = getDestId(radio.id);
                        //createUploader(nodeid);
                        //uploadForm();
								currentSelectedNodeId = getDestId(radio.id);
								changeCurrentView("Nodes");
								$("#logger").html("");
                } )
               )
               .append(
                $(document.createElement('label')).attr({
                        'for':  id
                })
                .text(item).css(css)
                ).append(document.createElement('br'))
	}
	container.buttonset();
	container.css({"text-align":"center"});
	container.show();
}
function runNodes(){
	if(currentState == "running"){
		alert("already running");
	}
		$.ajax({
		type:"POST",
		url: "/run",
		data:{"nc":NC,"sc":SC,"k":K,"n":N},
  		success: function(res){
		  				alert(res);
		  				currentState = "running";
		  				changeCurrentView("Nodes");
		  				 getLog();
  					}
  		});
  		
}

function initFileUpload()
{
 $('#file_upload').fileUploadUI({
       uploadTable: $('#files'),
       buildUploadRow: function (files, index) {
        var file = files[index];
        return $(
            '<tr>' +
            '<td class="file_upload_start">' +
            '<div class="ui-state-default ui-corner-all" title="Start Upload">' +
            '<span class="ui-icon ui-icon-circle-arrow-n">Start Upload<\/span>' +
            '<\/div>' +
            '<\/td>' +
            '<td>' + file.name + '<\/td>' +
            '<td class="file_upload_progress"><div><\/div><\/td>' +
            '<td class="file_upload_cancel">' +
            '<button class="ui-state-default ui-corner-all" title="Cancel">' +
            '<span class="ui-icon ui-icon-cancel">Cancel<\/span>' +
            '<\/button>' +
            '<\/td>' +
            '<\/tr>'
        );
    },
    beforeSend: function (event, files, index, xhr, handler, callBack) {
        handler.uploadRow.find('.file_upload_start').click(function () {
            callBack();
        });
    },
	 onLoad : function (event, files, index, xhr, handler) {
    				/* var json;
    				if (typeof xhr.responseText !== undef) {
        					json = $.parseJSON(xhr.responseText);
    					} else {
        					// Instead of an XHR object, an iframe is used for legacy browsers:
        					json = $.parseJSON(xhr.contents().text());
    					} */
    					//handler.uploadRow.remove();
    					handler.removeNode(handler.uploadRow);
    					//alert(xhr.responseText);
    					$("#uploadResults").html(xhr.responseText);
			},
	onAbort: function (event, files, index, xhr, handler) {
    			handler.removeNode(handler.uploadRow);
    			alert("aborted");
		}
    });
    
}

function mystore(){
  $.ajax({
		type:"POST",
		url: "/mystore",
		data:"dest="+currentSelectedNodeId,
  		success: function(res){
		  				//alert(res);
		  				$("#myStoreDiv").html(res);
  					}
  		});
}

function explore(){

 $.ajax({
		type:"POST",
		url: "/explore",
		data:"dest="+currentSelectedNodeId,
  		success: function(res){
  		
		  				res = jQuery.parseJSON(res);
		  				SI=res.SI;
		  				SIZES=res.sizes;
		  				subSI=res.subsi;
		  				
		  
		  				var stab = $("#storageTable");
		  				$("#storageTablebody").html("");
		  				for(var i=0;i<SI.length;i++)
		  				{
		  					var sid = "node"+i;
		  					var row = $("<tr>").attr({id:sid});
		  					
		  					$("<td>").text(SI[i]).appendTo(row);
		  					$("<td>").text(SIZES[i]).appendTo(row);
		  					$("<td>").text("----").appendTo(row);
		  					
		  					row.appendTo(stab);
		  					
							var mySub = subSI[i];
		  					var pieceSize = SIZES[i]/mySub.length;
		  					for(var k=0;k<mySub.length;k++)
		  					{
		  						var row=$("<tr>").addClass("child-of-"+sid);
		  						
								row.append($("<td>").text(mySub[k]));
		  						row.append($("<td>").text(pieceSize));
								
								var buttonId="dump."+SI[i]+"."+mySub[k];
								//var buttonId = "dump"+i+k;
		  						var button = $("<button>").
		  												attr({id: buttonId}).text("dump")
		  												.click(function(){
															var id = $(this)[0].id;
															alert(id);
		  												});
		  						$("#"+buttonId).button();
		  						
								row.append($("<td>").append(button));
		  						
		  						row.appendTo(stab);
		  					}
						}
						
						stab.treeTable();
		  				//alert(SI);
		  				//alert(SIZES);
						
  					}
  		});
}

function nodesTabs(i)
{
	//alert(i);
//UploadTab					
	if(i==0){
		$("#destId").val(currentSelectedNodeId);
	}
	//myStoreTab
	else if(i==1){
		mystore();
	}
	//StorageTab
	else if(i==2){
		explore();			
	}
}
function showNodePane()
{
	if(currentSelectedNodeId == 0 || currentSelectedNodeId == -1 ){
		currentSelectedNodeId = 1;
		//$('input[name=radio]:eq(0)').attr('checked', 'checked');
		$('input[name=radio]:checked + label').removeClass("ui-state-active");
		$('input[name=radio]:eq(0) + label').addClass("ui-state-active");
		$("#node1").checked = true;
	}
	
	if(currentSelectedNodeId > SC){
		$("#storageTabA").hide();
		$("#storageTab").hide();	
	}
	else{
		$("#storageTabA").show();
		$("#storageTab").show();	
	}
	$("#nodePane").show();
	$("#uploadResults").html("");
	$("#nodesWestDiv").show();
	
	var selected = $("#nodePane").tabs( "option", "selected" );	
	nodesTabs(selected);
}

function showMonitorPane()
{
	currentSelectedNodeId=0;
	$("#monitorPane").show();
	$("#monitorWestDiv").show();
}

function showIntroducerPane()
{
	currentSelectedNodeId=-1;
	$("#introducerPane").show();
	$("#introducerWestDiv").show();
	
}
function changeCurrentView(header)
{
	if(header == "Nodes")
	{
		checkState(showNodePane);
		$("#monitorPane").hide();
		$("#introducerPane").hide();
		
		$("#monitorWestDiv").hide();
		$("#introducerWestDiv").hide();
	}
	else if(header == "Monitor")
	{
		checkState(showMonitorPane);
		$("#nodePane").hide();
		$("#introducerPane").hide();
		
		$("#nodesWestDiv").hide();
		$("#introducerWestDiv").hide();
	}
	else if(header == "Introducer")
	{
		checkState(showIntroducerPane);
		$("#monitorPane").hide();
		$("#nodePane").hide();
		
		$("#monitorWestDiv").hide();
		$("#nodesWestDiv").hide();
	}
		showCurrentNode();
	$("#logger").html("");
}

function checkState(callback)
{
	if(currentState == "running"){
		callback();
	}
	else{
		alert("you should run the grid to interact with nodes");
	}
} 

function showCurrentNode(){
	var nodename;
	if(currentSelectedNodeId==0){
		nodename = "Monitor";	
	}
	else if(currentSelectedNodeId == -1){
		nodename = "Introducer";	
	}
	else{
		nodename = "node"+currentSelectedNodeId;
	}
	
	$("#currentNode").html("<b>Current Selected Node:</b>  " + nodename);
}
function getDestId(nodeid){
	return nodeid.substr(4);
}