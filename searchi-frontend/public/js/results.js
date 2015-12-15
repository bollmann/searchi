var twitterQuery = '';
var amazonQuery = '';
var weatherLoc = '';

$(function(){
	$('#searchButton').click(function(e){
		var query = encodeURI($('#searchBox').val()).replace(/#/g, '%23');
		if(query){
			window.location.href = "results?q=" + query;
		}
	});
});

$(function(){
	$('#searchBox').keypress(function(e) {
		disableTwitterButton();
		disableAmazonButton();
	    if(e.which == 13) {
			var query = encodeURI($('#searchBox').val()).replace(/#/g, '%23');
			if(query){
				window.location.href = "results?q=" + query;
			}	
			return false;    	
	    }
	});
});

$(document).ready(function() {
	disableTwitterButton();
	disableAmazonButton();

	var query = $('#searchBox').val();
	var parameters = {q: query};
	$.get('/search', parameters, function(data){
		$('#searchResults').html(data);
		loadResultSnippets(query);
	})

	analyseQuery(query.toLowerCase());
});

function analyseQuery(query){

	weatherLoc = '';
	if(query.indexOf('weather') > -1 || query.indexOf('climate') > -1 || query.indexOf('temperature') > -1 ||
		query.indexOf('forecast') > -1)
		weatherLoc = encodeURI(query.replace(/weather/g, '')
						.replace(/climate/g, '')
						.replace(/temperature/g, '')
						.replace(/today/g, '')
						.replace(/forecast/g, '')
						.replace(/\bnow\b/g, '')
						.replace(/\bwhat\b/g, '')
						.replace(/\bat\b|\bis\b|\bin\b|\bthe\b/g, '')
						.trim())

	$.get('/weather', {location: weatherLoc}, function(data){
		console.log(data)
		$('#weatherResult').html(data)
	})

	var words = query.trim().split(" ");
	for(var i = 0; i < words.length; i++){
		if(words[i].indexOf('twitter') > -1 ||
			~words[i].indexOf('@') ||
			~words[i].indexOf('#')){
			enableTwitterButton();
			twitterQuery = query.trim();
		}
		if(words[i].indexOf('shop') > -1 ||
			words[i].indexOf('amazon') > -1 ||
			words[i].indexOf('buy') > -1 ||
			words[i].indexOf('present') > -1 ||
			words[i].indexOf('gift') > -1){
			enableAmazonButton();
			amazonQuery = query.replace('amazon', '')
								.replace('buy', '')
								.trim();
		}
	}

}

function enableTwitterButton(){
	$('#twitterSearchBtn').removeAttr('disabled')
}

function disableTwitterButton(){
	$('#twitterSearchBtn').attr('disabled', 'disabled')	
}

function enableAmazonButton(){
	$('#amazonSearchBtn').removeAttr('disabled')
}

function disableAmazonButton(){
	$('#amazonSearchBtn').attr('disabled', 'disabled')	
}

function loadResultSnippets (query){
	var allResults = $('#searchResults').find($('p.list-group-item-text'))
	allResults.each(function(index, val){
		insertSnippet(val, query)
	});
}

function insertSnippet(listElement, query){
	var parameters = {url: $(listElement).attr('url')}
	$.get('./snippet', parameters, function(data){
		$(listElement).html(highlightSnippet(data, query))
	})
}

RegExp.escape = function(str) 
{
  var specials = /[.*+?|()\[\]{}\\$^]/g; // .*+?|()[]{}\$^
  return str.replace(specials, "\\$&");
}

function highlightSnippet(text, query){
	var highlightWord = query.split(" ")[0].trim();
	var regex = new RegExp("(" + RegExp.escape(highlightWord) + ")", "gi");
  	var boldedText = text.replace(regex, "<b>$1</b>");
  	var firstOccurrence = boldedText.toUpperCase().indexOf(highlightWord.toUpperCase())
  	var startMargin = firstOccurrence - 200
  	if(startMargin < 0)
  		startMargin = 0
  	var endMargin = firstOccurrence + 200
  	if(endMargin > text.length)
  		endMargin = text.length - 1
  	return boldedText.substring(startMargin, endMargin) + '...'
}

function loadAmazonDetails (){
	var allThumbnails = $('#amazonResults').find($("img"));
	allThumbnails.each(function(index, val){
		changeAmazonThumbnail(val)
	});
}

function changeAmazonThumbnail(thumbnailElement){
	var parameters = {asin: $(thumbnailElement).attr('asin')};
	$.get('./amazonImg', parameters, function(data){
		$(thumbnailElement).attr('src', data)
	})
}
$(function(){
	$('#twitterSearchBtn').click(function(e) {
		if(!$(this).hasClass('active')){
			$(this).addClass('active')
			$('#localSearchBtn').removeClass('active')
			$('#amazonSearchBtn').removeClass('active')
			
			$('#searchResults').html('<div class="progress"><div class="progress-bar progress-bar-striped active" role="progressbar" aria-valuenow="45" aria-valuemin="0" aria-valuemax="100" style="width: 100%"><span class="sr-only">Loading...</span></div></div>')
			var parameters = {q: twitterQuery};
			$.get('/twitter', parameters, function(data){
				$('#searchResults').html(data);
			})
		}
	});
});

$(function(){
	$('#amazonSearchBtn').click(function(e) {
		if(!$(this).hasClass('active')){
			$(this).addClass('active')
			$('#localSearchBtn').removeClass('active')
			$('#twitterSearchBtn').removeClass('active')
			
			$('#searchResults').html('<div class="progress"><div class="progress-bar progress-bar-striped active" role="progressbar" aria-valuenow="45" aria-valuemin="0" aria-valuemax="100" style="width: 100%"><span class="sr-only">Loading...</span></div></div>')
			var parameters = {q: amazonQuery};
			$.get('/amazon', parameters, function(data){
				$('#searchResults').html(data);
				loadAmazonDetails();
			})
		}
	});
});

$(function(){
	$('#localSearchBtn').click(function(e) {
		if(!$(this).hasClass('active')){
			$(this).addClass('active')
			$('#twitterSearchBtn').removeClass('active')
			$('#amazonSearchBtn').removeClass('active')
			
			$('#searchResults').html('<div class="progress"><div class="progress-bar progress-bar-striped active" role="progressbar" aria-valuenow="45" aria-valuemin="0" aria-valuemax="100" style="width: 100%"><span class="sr-only">Loading...</span></div></div>')
			var query = $('#searchBox').val();
			var parameters = {q: query};
			$.get('/search', parameters, function(data){
				$('#searchResults').html(data);
				loadResultSnippets(query);
			})
		}
	});
});