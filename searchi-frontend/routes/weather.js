var express = require('express');
var router = express.Router();
var request = require('request');
var jade = require('jade');
var path = require('path');
var util = require('util');

router.get('/', function(req, res, next) {
	var url = 'http://api.openweathermap.org/data/2.5/weather?q=' + req.query.location + '&APPID=2de143494c0b295cca9337e1e96b00e0&units=imperial';
	request(url, function(err, resp, body){
		if(err)
			console.log(err)
		body = JSON.parse(body)
		res.send(jade.renderFile(path.join(__dirname, '../views/weatherResult.jade'), {weather: body}))
	});
});

module.exports = router;
