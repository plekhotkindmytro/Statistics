$(document).ready(function() {

	$("#startGame").on("click", function() {
		var gameName = $('#gameName').val(), event = {};
		if (!gameName) {
			alert("Enter game name!");
			return;
		}
		event.type = "start_game";
		event.gameName = gameName;
		

		$.ajax({
			type : 'POST',
			url : "/events",
			cache : false,
			contentType : "application/json; charset=utf-8",
			dataType : "json",
			data : JSON.stringify(event),
			success : function(data) {
				console.log("game started");
			}
		});
	});

	$("#endGame").on("click", function() {
		var gameName = $('#gameName').val(), event = {};
		if (!gameName) {
			alert("Enter game name!");
			return;
		}
		event.type = "end_game";
		event.gameName = gameName;
		

		$.ajax({
			type : 'POST',
			url : "/events",
			cache : false,
			contentType : "application/json; charset=utf-8",
			dataType : "json",
			data : JSON.stringify(event),
			success : function(data) {
				console.log("game ended");
			}
		});
	});

	$("#score").on("click", function() {
		var gameName = $('#gameName').val(), event = {};
		if (!gameName) {
			alert("Enter game name!");
			return;
		}

		event.type = "score";
		event.gameName = gameName;
		

		$.ajax({
			type : 'POST',
			url : "/events",
			cache : false,
			contentType : "application/json; charset=utf-8",
			dataType : "json",
			data : JSON.stringify(event),
			success : function(data) {
				console.log("scored");
			}
		});
	});

	$("#drop").on("click", function() {
		var gameName = $('#gameName').val(), event = {};
		if (!gameName) {
			alert("Enter game name!");
			return;
		}

		event.type = "drop";
		event.gameName = gameName;
		

		$.ajax({
			type : 'POST',
			url : "/events",
			cache : false,
			contentType : "application/json; charset=utf-8",
			dataType : "json",
			data : JSON.stringify(event),
			success : function(data) {
				console.log("droped");
			}
		});
	});

	$("#dropZone").on("click", function() {
		var gameName = $('#gameName').val(), event = {};
		if (!gameName) {
			alert("Enter game name!");
			return;
		}

		event.type = "dropZone";
		event.gameName = gameName;
				

		$.ajax({
			type : 'POST',
			url : "/events",
			cache : false,
			contentType : "application/json; charset=utf-8",
			dataType : "json",
			data : JSON.stringify(event),
			success : function(data) {
				console.log("droped in zone");
			}
		});
	});

	$(".throw-backhand").on("click", function(e) {
		var gameName = $('#gameName').val(), event = {};
		if (!gameName) {
			alert("Enter game name!");
			return;
		}

		event.type = "throw-backhand";
		event.gameName = gameName;
		event.player = $(this).attr("id");

		$.ajax({
			type : 'POST',
			url : "/events",
			cache : false,
			contentType : "application/json; charset=utf-8",
			dataType : "json",
			data : JSON.stringify(event),
			success : function(data) {
				console.log("thrown backhand");
			}
		});
	});
	
	$(".throw-forehand").on("click", function(e) {
		var gameName = $('#gameName').val(), event = {};
		if (!gameName) {
			alert("Enter game name!");
			return;
		}

		event.type = "throw_forehand";
		event.gameName = gameName;
		event.player = $(this).attr("id");

		$.ajax({
			type : 'POST',
			url : "/events",
			cache : false,
			contentType : "application/json; charset=utf-8",
			dataType : "json",
			data : JSON.stringify(event),
			success : function(data) {
				console.log("thrown forehand");
			}
		});
	});
	
	$(".throw-other").on("click", function(e) {
		var gameName = $('#gameName').val(), event = {};
		if (!gameName) {
			alert("Enter game name!");
			return;
		}

		event.type = "throw_other";
		event.gameName = gameName;
		event.player = $(this).attr("id");

		$.ajax({
			type : 'POST',
			url : "/events",
			cache : false,
			contentType : "application/json; charset=utf-8",
			dataType : "json",
			data : JSON.stringify(event),
			success : function(data) {
				console.log("thrown other");
			}
		});
	});

});