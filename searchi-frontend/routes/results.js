var express = require('express');
var router = express.Router();

router.get('/', function(req, res, next) {
	var q = decodeURI(req.query.q);
  	res.render('resultsLoading', {q: q});
});

module.exports = router;
