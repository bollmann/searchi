var express = require('express');
var router = express.Router();
var request = require('request');
var jade = require('jade');
var path = require('path');
var fs = require('fs');

router.get('/', function(req, res, next) {
	var ips = JSON.parse(fs.readFileSync('ips.json', 'utf8'))
	var url = ips.servlet + "searchInterface/image?q=" + req.query.q
  	request(url, function(err, resp, body){
		if(body && typeof body != 'undefined' && body.indexOf('<') < 0){
			body = JSON.parse(body)
			if(body.images.length > 0)
				htmlResults = jade.renderFile(path.join(__dirname, '../views/imageResults.jade'), {urls: body.images, q: req.query.q})
			else
				htmlResults = jade.renderFile(path.join(__dirname, '../views/noImageResults.jade'), {query: req.query.q})
			res.send(htmlResults)
		} else {
	  		var htmlResults = jade.renderFile(path.join(__dirname, '../views/noImageResults.jade'), {query: req.query.q})
	  		res.send(htmlResults)
		}
  	});
});

module.exports = router;
