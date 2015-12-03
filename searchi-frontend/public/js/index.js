$(function(){
	$('#searchButton').click(function(e){
		var query = $('#searchBox').val();
		if(query){
			var parameters = {q: query };
			$.get('/search', parameters, function(data){
				$('#searchResults').html(data);
			})
		}
 });
});
