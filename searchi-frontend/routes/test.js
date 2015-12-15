var express = require('express');
var router = express.Router();
var request = require('request');
var jade = require('jade');
var path = require('path');

router.get('/', function(req, res, next) {
	res.render('test');
});

module.exports = router;
