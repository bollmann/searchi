var express = require('express');
var router = express.Router();
var request = require('request');
var jade = require('jade');
var path = require('path');
var htmlToText = require('html-to-text')

router.get('/', function(req, res, next) {
  var url = req.query.url;
  if(url.indexOf('youtube.com/watch?') != -1){
    var embedURL = url.replace('/watch?v=', '/embed/').replace('https', 'http')
    var text = jade.renderFile(path.join(__dirname, '../views/youtubeEmbed.jade'), {url: embedURL});
    res.send(text);
  } else {
	  request(url, function(err, resp, body){
	  	var text = htmlToText.fromString(body).replace(/(\[.*\])/g, '').trim()
	  	res.send(text);
  	});
  }

});

module.exports = router;
