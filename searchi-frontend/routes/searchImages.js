var express = require('express');
var router = express.Router();
var request = require('request');
var jade = require('jade');
var path = require('path');

router.get('/', function(req, res, next) {
	var htmlResults = jade.renderFile(path.join(__dirname, '../views/imageResults.jade'), {q: req.query.q})
	res.send(htmlResults)
});

module.exports = router;
