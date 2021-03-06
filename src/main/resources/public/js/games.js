function drawZoneSuccessFail() {
	$.ajax({
		type : 'GET',
		url : "/games/zoneSuccessFail",
		cache : false,
		contentType : "application/json; charset=utf-8",
		dataType : "json",
		success : function(stats) {
			var i, len, gameName, scores, failedTries, rows = [];
			rows.push([ "Гра", "Кiлькiсть занесених поiнтiв",
					"Кiлькiсть провалених кидкiв в зону" ]);
			for (len = stats.length, i = 0; i < len; ++i) {
				gameName = stats[i].gameName;
				scores = stats[i].scores;
				failedTries = stats[i].failedTries;
				rows.push([ gameName, scores, failedTries ]);
			}

			var data = google.visualization.arrayToDataTable(rows);

			var options = {
				chart : {
					title : 'Вiдсоток успiшних кидкiв в зону'
				},
				height : 400,
				colors : [ '#1b9e77', '#d95f02' ]
			};

			var chart = new google.charts.Bar(document
					.getElementById('chart-zoneSuccessFail'));

			chart.draw(data, options);
		}
	});
}

function drawPassCountFrequencySuccess() {
	$
			.ajax({
				type : 'GET',
				url : "/games/points/success/passCountFrequency",
				cache : false,
				contentType : "application/json; charset=utf-8",
				dataType : "json",
				success : function(stats) {
					var i, len, passCount, frequency, rows = [];
					rows.push([ "Кiлькiсть послiдовних пасiв", "Частота" ]);
					for (len = stats.length, i = 0; i < len; ++i) {
						passCount = stats[i].passCount;
						frequency = stats[i].frequency;
						rows.push([ passCount, frequency ]);
					}

					var data = google.visualization.arrayToDataTable(rows);

					var options = {
						chart : {
							title : 'Графiк частоти послiдовних пасiв в проваленій атацi'
						},
						height : 400,
						colors : [ '#1b9e77' ]
					};

					var chart = new google.charts.Bar(document
							.getElementById('chart-passCountFrequencySuccess'));

					chart.draw(data, options);
				}
			});
}

function drawPassCountFrequencyFail() {
	$
			.ajax({
				type : 'GET',
				url : "/games/points/fail/passCountFrequency",
				cache : false,
				contentType : "application/json; charset=utf-8",
				dataType : "json",
				success : function(stats) {
					var i, len, passCount, frequency, rows = [];
					rows.push([ "Кiлькiсть послiдовних пасiв", "Частота" ]);
					for (len = stats.length, i = 0; i < len; ++i) {
						passCount = stats[i].passCount;
						frequency = stats[i].frequency;
						rows.push([ passCount, frequency ]);
					}

					var data = google.visualization.arrayToDataTable(rows);

					var options = {
						chart : {
							title : 'Графiк частоти послiдовних пасiв в провалених атацi'
						},
						height : 400,
						colors : [ '#d95f02' ]
					};

					var chart = new google.charts.Bar(document
							.getElementById('chart-passCountFrequencyFail'));

					chart.draw(data, options);
				}
			});
}