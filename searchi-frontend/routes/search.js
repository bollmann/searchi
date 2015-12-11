var express = require('express');
var router = express.Router();
var request = require('request');
var jade = require('jade');
var path = require('path');

/* GET home page. */
var json = { pagerank: { time: { time: '121' } },
  indexer: 
   { '0': 
      { rank: '2.797016143798828',
        snippet: 'DocID 91341 ; score=2.797016\nhelp features={docId: 91341, maxtf: 1.000000, euclidtf: 0.248282, tfidf: 0.693147, totalCount: 3,linkCount: 3, metaTagCount: 0, headerCount: 0, wordPositions: []}\n',
        url: 'http://www.cshlp.org/ghg5/search_text_top.shtml' },
     '1': 
      { rank: '2.797016143798828',
        snippet: 'DocID 95538 ; score=2.797016\nhelp features={docId: 95538, maxtf: 1.000000, euclidtf: 0.457604, tfidf: 0.693147, totalCount: 14,linkCount: 12, metaTagCount: 0, headerCount: 1, wordPositions: []}\n',
        url: 'http://mhhauto.com/misc.php?action=help' },
     '2': 
      { rank: '2.797016143798828',
        snippet: 'DocID 8897 ; score=2.797016\nhelp features={docId: 8897, maxtf: 1.000000, euclidtf: 0.244339, tfidf: 0.693147, totalCount: 2,linkCount: 2, metaTagCount: 0, headerCount: 0, wordPositions: []}\n',
        url: 'https://myaccount.dropsend.com/login' },
     '3': 
      { rank: '2.797016143798828',
        snippet: 'DocID 52351 ; score=2.797016\nhelp features={docId: 52351, maxtf: 1.000000, euclidtf: 0.570137, tfidf: 0.693147, totalCount: 12,linkCount: 3, metaTagCount: 0, headerCount: 5, wordPositions: []}\n',
        url: 'http://viesearch.com/137ri/matlab-assignment-helpmatlab-project-helpmatlab-homework-help' },
     '4': 
      { rank: '2.797016143798828',
        snippet: 'DocID 39363 ; score=2.797016\nhelp features={docId: 39363, maxtf: 1.000000, euclidtf: 0.516815, tfidf: 0.693147, totalCount: 17,linkCount: 17, metaTagCount: 0, headerCount: 0, wordPositions: []}\n',
        url: 'http://www.timecardcalculator.net/settings.php#pay_ot' },
     '5': 
      { rank: '2.797016143798828',
        snippet: 'DocID 711 ; score=2.797016\nhelp features={docId: 711, maxtf: 1.000000, euclidtf: 0.201008, tfidf: 0.693147, totalCount: 2,linkCount: 1, metaTagCount: 0, headerCount: 0, wordPositions: []}\n',
        url: 'http://www.spamarrest.com' },
     '6': 
      { rank: '2.797016143798828',
        snippet: 'DocID 92096 ; score=2.797016\nhelp features={docId: 92096, maxtf: 1.000000, euclidtf: 0.357450, tfidf: 0.693147, totalCount: 14,linkCount: 1, metaTagCount: 3, headerCount: 5, wordPositions: []}\n',
        url: 'http://www.citymax.com/features/help-and-support.htm' },
     '7': 
      { rank: '2.797016143798828',
        snippet: 'DocID 60626 ; score=2.797016\nhelp features={docId: 60626, maxtf: 1.000000, euclidtf: 0.450910, tfidf: 0.693147, totalCount: 7,linkCount: 4, metaTagCount: 0, headerCount: 2, wordPositions: []}\n',
        url: 'http://www.econbiz.de/eb/en/hilfe' },
     '8': 
      { rank: '2.797016143798828',
        snippet: 'DocID 79145 ; score=2.797016\nhelp features={docId: 79145, maxtf: 1.000000, euclidtf: 0.306280, tfidf: 0.693147, totalCount: 16,linkCount: 3, metaTagCount: 0, headerCount: 9, wordPositions: []}\n',
        url: 'http://help.com' },
     '9': 
      { rank: '2.797016143798828',
        snippet: 'DocID 110048 ; score=2.797016\nhelp features={docId: 110048, maxtf: 1.000000, euclidtf: 0.239474, tfidf: 0.693147, totalCount: 4,linkCount: 4, metaTagCount: 0, headerCount: 1, wordPositions: []}\n',
        url: 'http://www.ptd.net' },
     '10': 
      { rank: '2.797016143798828',
        snippet: 'DocID 44848 ; score=2.797016\nhelp features={docId: 44848, maxtf: 1.000000, euclidtf: 0.381771, tfidf: 0.693147, totalCount: 6,linkCount: 6, metaTagCount: 0, headerCount: 1, wordPositions: []}\n',
        url: 'http://www.trafficmonsoon.net' },
     time: { time: '11110' } },
  combined: 
   { '0': 
      { rank: '2.797016143798828',
        url: 'http://www.timecardcalculator.net/settings.php#pay_ot' },
     '1': 
      { rank: '2.797016143798828',
        url: 'http://mhhauto.com/misc.php?action=help' },
     '2': 
      { rank: '2.797016143798828',
        url: 'https://myaccount.dropsend.com/login' },
     '3': { rank: '2.797016143798828', url: 'http://www.ptd.net' },
     '4': 
      { rank: '2.797016143798828',
        url: 'http://viesearch.com/137ri/matlab-assignment-helpmatlab-project-helpmatlab-homework-help' },
     '5': 
      { rank: '2.797016143798828',
        url: 'http://www.trafficmonsoon.net' },
     '6': { rank: '2.797016143798828', url: 'http://help.com' },
     '7': 
      { rank: '2.797016143798828',
        url: 'http://www.citymax.com/features/help-and-support.htm' },
     '8': 
      { rank: '2.797016143798828',
        url: 'http://www.econbiz.de/eb/en/hilfe' },
     '9': { rank: '2.797016143798828', url: 'http://www.spamarrest.com' },
     '10': 
      { rank: '2.797016143798828',
        url: 'http://www.cshlp.org/ghg5/search_text_top.shtml' },
     time: { time: '1' } } };

router.get('/', function(req, res, next) {
	var url = "http://192.168.0.100:8080/searchInterface?q=" + req.query.q
	// htmlResults = jade.renderFile(path.join(__dirname, '../views/resultsList.jade'), json)
	// res.send(htmlResults)
	request(url, function(err, resp, body){
		body = JSON.parse(body);
		console.log(body);
		if(body.indexer)
			htmlResults = jade.renderFile(path.join(__dirname, '../views/resultsList.jade'), body)
		else
			htmlResults = jade.renderFile(path.join(__dirname, '../views/noResults.jade'), {query: req.query.q})
		res.send(htmlResults)
	});	
});

module.exports = router;
