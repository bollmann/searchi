$(function(){
	$('#searchBox').keypress(function(e) {
	    if(e.which == 13) {
			var query = $('#searchBox').val();
			if(query){
				window.location.href = "http://192.168.0.100:3000/results?q=" + query;
			}	
			return false;    	
	    }
	});
});