var express = require('express');
var router = express.Router();

/* GET home page. */
router.get('/', function(req, res, next) {
  res.send('Search results for: <b>' + req.query.q + '</b>');
});

module.exports = router;
