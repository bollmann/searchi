var express = require('express');
var router = express.Router();
var request = require('request');
var jade = require('jade');
var path = require('path');
var aws = require("aws-lib");
var util = require('util');
var htmlToText = require('html-to-text')
router.get('/', function(req, res, next) {
  var url = req.query.url;
  request(url, function(err, resp, body){
  	//res.send(body);
  	var text = htmlToText.fromString(body).replace(/(\[.*\])/g, '').trim()
  	res.send(text);
  });

});

module.exports = router;
