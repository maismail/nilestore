function init() {
	$("#nodePane").tabs({
		select : function(event, ui) {
			var i = ui.index;
			nodesTabs(i);
			return true;
		}
	});

	$("#nodePane").tabs("select", 0);
	initFileUpload();
	$("#myStoreDiv").css({
		"text-align" : "center"
	});
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
	$.ajax({
		type : "POST",
		url : "/mystore",
		dataType : "text",
		success : function(res) {
			// alert(res);
			$("#myStoreDiv").html(res);
		}
	});
}

function explore() {

	$.ajax({
		type : "POST",
		url : "/explore",
		dataType : "json",
		success : function(res) {
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
	// UploadTab
	if (i == 0) {
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
