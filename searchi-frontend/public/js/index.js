$(function(){
	$('#searchBox').keypress(function(e) {
	    if(e.which == 13) {
			var query = $('#searchBox').val();
			if(query){
				window.location.href = "results?q=" + query;
			}	
			return false;    	
	    }
	});
});