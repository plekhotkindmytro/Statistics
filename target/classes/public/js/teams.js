function drawTable(data, tableSelector) {
	for (var i = 0; i < data.length; i++) {
		drawRow(data[i], tableSelector);
	}
}

function drawRow(rowData, tableSelector) {
	var row = $("<tr />")
	$(tableSelector).append(row);
	//row.append($("<td>" + rowData.id + "</td>"));
	row.append($("<td>" + rowData.name + "</td>"));
}

function get() {
	$.ajax({
		type : 'GET',
		url : "/teams",
		cache : false,
		contentType : "application/json; charset=utf-8",
		dataType : "json",
		data : "",
		success : function(data) {
			drawTable(data, "#teamsTable");
		}
	});
}

function create() {
	var team = {};
	team.name = $("#teamName").val();
	team.players = [];
	$.ajax({
		type : 'POST',
		url : "/teams",
		cache : false,
		contentType : "application/json; charset=utf-8",
		dataType : "json",
		data : JSON.stringify(team),
		success : function(data) {
			get();
		}
	});
}

$(document).ready(function() {
	get();

	$("#createButton").on("click", function() {
		create();
	});
});