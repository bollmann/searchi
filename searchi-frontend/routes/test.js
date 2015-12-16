var express = require('express');
var router = express.Router();
var request = require('request');
var jade = require('jade');
var path = require('path');
var fs = require('fs');


router.get('/', function(req, res, next) {
  var results = JSON.parse(fs.readFileSync('results.json'))
  console.log(results.combined)
  res.render('test', {results: results.combined})
});

module.exports = router;
