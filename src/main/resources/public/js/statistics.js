//Load the Visualization API and the piechart package.
google.load('visualization', '1.0', {
	'packages' : [ 'corechart' ]
});
// Set a callback to run when the Google Visualization API is loaded.
google.setOnLoadCallback(drawAll);

function drawAll() {
	$.ajax({
		type : 'GET',
		url : "/players/stats",
		cache : false,
		contentType : "application/json; charset=utf-8",
		dataType : "json",
		success : function(stats) {
			var i, len, playerStats;
			$("#stats").html("");
			for (len = stats.length, i = 0; i < len; ++i) {
				playerStats = stats[i];
				drawPlayerStats(playerStats);
			}
			playerComparisonChart(stats);
		}
	});
}

function endsWith(str, suffix) {
	return str.indexOf(suffix, str.length - suffix.length) !== -1;
}

function drawPlayerStats(playerStats) {
	$("#stats")
			.append(
					'<h2>'
							+ playerStats.name
							+ ', '
							+ playerStats.tshirtNumber
							+ '</h2><div class="row"><div class="col-md-6"><div id="chart1-'
							+ playerStats.tshirtNumber
							+ '"></div></div><div class="col-md-6"><div id="chart2-'
							+ playerStats.tshirtNumber
							+ '"></div></div></div><hr>');
	// $("#chart-"+playerStats.tshirtNumber)

	successFailChart(playerStats);
	throwTypesChart(playerStats);
}
function successFailChart(playerStats) {
	var data = new google.visualization.DataTable();
	data.addColumn('string', 'Кидок');
	data.addColumn('number', 'Кількість');

	var rows = [], len, i, events = playerStats.events, type, successful = 0, failed = 0;
	for (len = events.length, i = 0; i < len; ++i) {
		type = events[i].type;

		if (type === "throw_other" || type === "throw-backhand"
				|| type === "throw_forehand" || endsWith(type, "-score")) {
			successful += events[i].count;
		} else if (type === "drop" || type === "dropZone") {
			failed += events[i].count;
		}

	}
	rows.push([ "Успішний кидок", successful ]);
	rows.push([ "Втрата диску", failed ]);
	data.addRows(rows);

	// Set chart options
	var options = {
		'title' : 'Відсоток успішних пасів',
		'width' : 600,
		'height' : 300
	};

	// Instantiate and draw our chart, passing in some options.
	var chart = new google.visualization.PieChart(document
			.getElementById('chart1-' + playerStats.tshirtNumber));
	chart.draw(data, options);
}

function throwTypesChart(playerStats) {
	var data = new google.visualization.DataTable();
	data.addColumn('string', 'Кидок');
	data.addColumn('number', 'Кількість');

	var rows = [], len, i, events = playerStats.events, type, backhand = 0, forehand = 0, other = 0;
	for (len = events.length, i = 0; i < len; ++i) {
		type = events[i].type;
		if (type === "throw-backhand") {
			backhand += events[i].count;
		} else if (type === "throw_forehand") {
			forehand += events[i].count;
		} else if (type === "throw_other") {
			other += events[i].count;
		}
	}

	rows.push([ "Бекхенд", backhand ]);
	rows.push([ "Форхенд", forehand ]);
	rows.push([ "Інший кидок (хамер, тощо)", other ]);

	data.addRows(rows);

	// Set chart options
	var options = {
		'title' : 'Типи кидків',
		'width' : 600,
		'height' : 300
	};

	// Instantiate and draw our chart, passing in some options.
	var chart = new google.visualization.PieChart(document
			.getElementById('chart2-' + playerStats.tshirtNumber));
	chart.draw(data, options);
}

function playerComparisonChart(stats) {
	var i, len, j, eventLen, successful, failed, events, type;
	var rows = [ [ 'Гравець', 'Паси', 'Втрати' ] ];
	for (len = stats.length, i = 0; i < len; ++i) {
		playerStats = stats[i];
		events = playerStats.events;
		successful = 0;
		failed = 0;
		for (eventLen = events.length, j = 0; j < eventLen; ++j) {
			type = events[j].type;
			if (type === "throw_other" || type === "throw-backhand"
					|| type === "throw_forehand" || endsWith(type, "-score")) {
				successful += events[j].count;
			} else if (type === "drop" || type === "dropZone") {
				failed += events[j].count;
			}
		}
		rows.push([ playerStats.name, successful, failed ]);
	}
	var data = google.visualization.arrayToDataTable(rows);

	var options = {
		title : 'Графік активності гравців на полі',
		width: '100%',
        height: 600,
		hAxis : {
			title : 'Активність',
			minValue : 0,
		},
		vAxis : {
			title : 'Гравець'
		}
	};
	var chart = new google.visualization.BarChart(document
			.getElementById('chart-player-comparison'));
	chart.draw(data, options);
}