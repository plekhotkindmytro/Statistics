//Load the Visualization API and the piechart package.
google.load('visualization', '1.1', {
	'packages' : [ 'corechart', 'bar', 'sankey' ]
});
// Set a callback to run when the Google Visualization API is loaded.
google.setOnLoadCallback(drawAll);

function drawAll() {
	// FROM games.js
	drawZoneSuccessFail();
	drawPassCountFrequencySuccess();
	drawPassCountFrequencyFail();

	// other
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
			playerActivityPercentChart(stats);
			playerFailPercentChart(stats);
			playerScorePercentChart(stats);
			playertPassScorePercenChart(stats);

			mostActivePlayer(stats);
			mostActivePlayerInField(stats);

			mostFailingPlayer(stats);
			mostAccuratePlayer(stats);
			leastAccuratePlayer(stats);
			bestLong(stats);
			bestZonePassPlayer(stats);

		}
	});

	passesBetweenPlayers();
}

function passesBetweenPlayers() {
	$.ajax({
		type : 'GET',
		url : "/passes",
		cache : false,
		contentType : "application/json; charset=utf-8",
		dataType : "json",
		success : function(passes) {
			var i, len, rows = [], data = new google.visualization.DataTable();

			data.addColumn('string', 'From');
			data.addColumn('string', 'To');
			data.addColumn('number', 'Кількість пасів');
			
			var pass = passes[0];
			$("#passFrom").html("<b>" + pass.from.replace("Від: ","")+"</b>");
			$("#passTo").html("<b>" + pass.to.replace("До: ","")+"</b>");
			for (len = passes.length, i = 0; i < len; ++i) {
				rows.push([ passes[i].from, passes[i].to, passes[i].count ]);
			}

			data.addRows(rows);

			var colors = [

			              ]
			// Sets chart options.
			var options = {
				width: "100%",
				height: 700,
				sankey: { 
					node: { 
						nodePadding: 50
					},
					iterations: 50
				}
				
			};

			// Instantiates and draws our chart, passing in some options.
			var chart = new google.visualization.Sankey(document
					.getElementById('chart-passes'));
			chart.draw(data, options);
		}
	});
}
function mostActivePlayer(stats) {
	var i, len, j, eventLen, events, type, successful;
	var most = {};
	for (len = stats.length, i = 0; i < len; ++i) {
		playerStats = stats[i];
		events = playerStats.events;
		successful = 0;
		for (eventLen = events.length, j = 0; j < eventLen; ++j) {
			type = events[j].type;
			if (type === "throw_other" || type === "throw-backhand"
					|| type === "throw_forehand" || endsWith(type, "score")
					|| type === "drop" || type === "dropZone") {
				successful += events[j].count;
			}
		}

		if (!most.count || (most.count && successful > most.count)) {
			most.name = playerStats.name;
			most.count = successful;
		}
	}

	$("#mostActive").html("<b>" + most.name + "</b>");
}

function mostActivePlayerInField(stats) {
	var i, len, j, eventLen, events, type, successful;
	var most = {};
	for (len = stats.length, i = 0; i < len; ++i) {
		playerStats = stats[i];
		events = playerStats.events;
		successful = 0;
		for (eventLen = events.length, j = 0; j < eventLen; ++j) {
			type = events[j].type;
			if (type === "throw_other" || type === "throw-backhand"
					|| type === "throw_forehand") {
				successful += events[j].count;
			}
		}

		if (!most.count || (most.count && successful > most.count)) {
			most.name = playerStats.name;
			most.count = successful;
		}
	}

	$("#mostActiveInField").html("<b>" + most.name + "</b>");
}

function mostFailingPlayer(stats) {
	var i, len, j, eventLen, events, type, successful;
	var most = {};
	for (len = stats.length, i = 0; i < len; ++i) {
		playerStats = stats[i];
		events = playerStats.events;
		successful = 0;
		for (eventLen = events.length, j = 0; j < eventLen; ++j) {
			type = events[j].type;
			if (type === "drop" || type === "dropZone") {
				successful += events[j].count;
			}
		}

		if (!most.count || (most.count && successful > most.count)) {
			most.name = playerStats.name;
			most.count = successful;
		}
	}

	$("#mostFailing").html("<b>" + most.name + "</b>");
}
function mostAccuratePlayer(stats) {
	var i, len, j, eventLen, events, type, successful, failed, count = 0;
	var most = {};
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
		count = successful * 1.0 / (successful + failed);
		if (!most.count || count > most.count) {
			most.name = playerStats.name;
			most.count = successful * 1.0 / (successful + failed);
		}
	}

	$("#mostAccurate").html("<b>" + most.name + "</b>");
}

function leastAccuratePlayer(stats) {
	var i, len, j, eventLen, events, type, successful, failed, count = 0;
	var most = {};
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
		count = failed * 1.0 / (successful + failed);
		if (!most.count || count > most.count) {
			most.name = playerStats.name;
			most.count = count;
		}
	}

	$("#leastAccurate").html("<b>" + most.name + "</b>");
}

function bestLong(stats) {
	var i, len, j, eventLen, events, type, successful;
	var most = {};
	for (len = stats.length, i = 0; i < len; ++i) {
		playerStats = stats[i];
		events = playerStats.events;
		successful = 0;

		for (eventLen = events.length, j = 0; j < eventLen; ++j) {
			type = events[j].type;
			if (type === "score") {
				successful += events[j].count;
			}
		}

		if (!most.count || (most.count && successful > most.count)) {
			most.name = playerStats.name;
			most.count = successful;
		}
	}

	$("#bestLong").html("<b>" + most.name + "</b>");
}

function bestZonePassPlayer(stats) {
	var i, len, j, eventLen, events, type, successful;
	var most = {};
	for (len = stats.length, i = 0; i < len; ++i) {
		playerStats = stats[i];
		events = playerStats.events;
		successful = 0;

		for (eventLen = events.length, j = 0; j < eventLen; ++j) {
			type = events[j].type;
			if (endsWith(type, "-score")) {
				successful += events[j].count;
			}
		}

		if (!most.count || (most.count && successful > most.count)) {
			most.name = playerStats.name;
			most.count = successful;
		}
	}

	$("#bestZonePass").html("<b>" + most.name + "</b>");
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
		width : '100%',
		height : 600,
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

function playerActivityPercentChart(stats) {
	var i, len, j, eventLen, successful, failed, events, type;
	var rows = [ [ 'Гравець', 'Паси' ] ];
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
			}
		}
		rows.push([ playerStats.name, successful ]);
	}

	var data = google.visualization.arrayToDataTable(rows);

	var options = {
		title : 'Відсоток успішних пасів',
		width : '100%',
		height : 600,
		sliceVisibilityThreshold : .1,
		pieResidueSliceLabel : "Інші",
		pieHole : 0.4
	};
	var chart = new google.visualization.PieChart(document
			.getElementById('chart-player-activity-percent'));
	chart.draw(data, options);
}

function playerFailPercentChart(stats) {
	var i, len, j, eventLen, failed, events, type;
	var rows = [ [ 'Гравець', 'Помилки' ] ];
	for (len = stats.length, i = 0; i < len; ++i) {
		playerStats = stats[i];
		events = playerStats.events;
		failed = 0;
		for (eventLen = events.length, j = 0; j < eventLen; ++j) {
			type = events[j].type;
			if (type === "drop" || type === "dropZone") {
				failed += events[j].count;
			}
		}
		rows.push([ playerStats.name, failed ]);
	}

	var data = google.visualization.arrayToDataTable(rows);

	var options = {
		title : 'Кількість помилок за турнір',
		width : '100%',
		height : 600,
		pieSliceText : "value",
		sliceVisibilityThreshold : .05,
		pieResidueSliceLabel : "Інші",
		pieHole : 0.4
	};
	var chart = new google.visualization.PieChart(document
			.getElementById('chart-player-fail-percent'));
	chart.draw(data, options);
}

function playerScorePercentChart(stats) {
	var i, len, j, eventLen, successful, failed, events, type;
	var rows = [ [ 'Гравець', 'Занесені поінти' ] ];
	for (len = stats.length, i = 0; i < len; ++i) {
		playerStats = stats[i];
		events = playerStats.events;
		successful = 0;
		failed = 0;
		for (eventLen = events.length, j = 0; j < eventLen; ++j) {
			type = events[j].type;
			if (type === "score") {
				successful += events[j].count;
			}
		}
		rows.push([ playerStats.name, successful ]);
	}

	var data = google.visualization.arrayToDataTable(rows);

	var options = {
		title : 'Кількість занесених поінтів',
		width : '100%',
		height : 600,
		pieSliceText : "value",
		sliceVisibilityThreshold : .05,
		pieResidueSliceLabel : "Інші",
		is3D : true
	};
	var chart = new google.visualization.PieChart(document
			.getElementById('chart-player-score-percent'));
	chart.draw(data, options);
}

function playertPassScorePercenChart(stats) {
	var i, len, j, eventLen, successful, failed, events, type;
	var rows = [ [ 'Гравець', 'Успішні паси в зону' ] ];
	for (len = stats.length, i = 0; i < len; ++i) {
		playerStats = stats[i];
		events = playerStats.events;
		successful = 0;
		failed = 0;
		for (eventLen = events.length, j = 0; j < eventLen; ++j) {
			type = events[j].type;
			if (endsWith(type, "-score")) {
				successful += events[j].count;
			}
		}
		rows.push([ playerStats.name, successful ]);
	}

	var data = google.visualization.arrayToDataTable(rows);

	var options = {
		title : 'Гольові паси в зону',
		width : '100%',
		height : 600,
		pieSliceText : "value",
		sliceVisibilityThreshold : .05,
		pieResidueSliceLabel : "Інші",
		is3D : true

	};
	var chart = new google.visualization.PieChart(document
			.getElementById('chart-player-pass-score-percent'));
	chart.draw(data, options);
}