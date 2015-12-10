var express = require('express');
var router = express.Router();
var request = require('request');
var jade = require('jade');
var path = require('path');
var aws = require("aws-lib");
var util = require('util');

router.get('/', function(req, res, next) {
  var prodAdv = aws.createProdAdvClient('AKIAIKMFFDKAKEFVAEGA', '7waph0aUEm/uFEizmic1eVAmsGK5b69weZJ9VK6S', 'searchi0f-20');
  var options = {SearchIndex: "Blended", Keywords: req.query.q}
  prodAdv.call("ItemSearch", options, function(err, result) {
    apparelBody = JSON.parse(JSON.stringify(result))
    //console.log(util.inspect(apparelBody, {showHidden: false, depth: null}));
    if(apparelBody.Items.TotalResults > 1)
      res.send(jade.renderFile(path.join(__dirname, '../views/amazonResults.jade'), {apparel: apparelBody.Items.Item}));
    else      
      res.send(jade.renderFile(path.join(__dirname, '../views/noResults.jade'), {query: req.query.q}));
  });

});

module.exports = router;
