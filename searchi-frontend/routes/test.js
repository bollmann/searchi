var express = require('express');
var router = express.Router();
var request = require('request');
var jade = require('jade');
var path = require('path');

router.get('/', function(req, res, next) {
	var url = "http://192.168.0.100:8080/searchInterface?q=obama";
	request(url, function(err, resp, body){	
		body = JSON.parse(body);
		console.log(jade.renderFile(path.join(__dirname, '../views/seults.jade'), {a: 'a'}))
		res.render('results', body);		
	})
});

module.exports = router;
