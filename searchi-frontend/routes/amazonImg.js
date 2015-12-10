var express = require('express');
var router = express.Router();
var request = require('request');
var jade = require('jade');
var path = require('path');
var aws = require("aws-lib");
var util = require('util');

router.get('/', function(req, res, next) {
  var prodAdv = aws.createProdAdvClient('AKIAIKMFFDKAKEFVAEGA', '7waph0aUEm/uFEizmic1eVAmsGK5b69weZJ9VK6S', 'searchi0f-20');
  var options = {ResponseGroup: 'Images', IdType: 'ASIN', ItemId: req.query.asin}
  prodAdv.call("ItemLookup", options, function(err, result) {
    apparelBody = JSON.parse(JSON.stringify(result))
    if(apparelBody.Items){
    	if(apparelBody.Items.Item){
    		if(apparelBody.Items.Item.MediumImage){
    			if(apparelBody.Items.Item.MediumImage.URL){
			    	console.log('<img class="media-object" src="' + apparelBody.Items.Item.MediumImage.URL + '"/>')
			    	res.send(apparelBody.Items.Item.MediumImage.URL)
			    }
	    	}
    	}
    }
    else
    	res.send('/images/thumbnail.png')
  });

});

module.exports = router;
