var express = require('express');
var router = express.Router();
var request = require('request');
var jade = require('jade');
var path = require('path');

/* GET home page. */
router.get('/', function(req, res, next) {
	var url = "http://192.168.0.100:8080/searchInterface?q=" + req.query.q;
	// request(url, function(err, resp, body){
	// 	body = JSON.parse(body);
	// 	console.log(body);
	// 	if(body.indexer)
	// 		htmlResults = jade.renderFile(path.join(__dirname, '../views/resultsList.jade'), body)
	// 	else
	// 		htmlResults = jade.renderFile(path.join(__dirname, '../views/noResults.jade'), {query: req.query.q})
	// 	res.send(htmlResults)
	// });	
	res.send("<h4>Decide algo.</h4>")
});

module.exports = router;
