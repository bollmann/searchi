var express = require('express');
var router = express.Router();
var request = require('request');
var jade = require('jade');
var path = require('path');
var Twitter = require('twitter');

router.get('/', function(req, res, next) {
  var client = new Twitter({
    consumer_key: 'uj0f6jjN4WWFFDEt8KPCvJulT',
    consumer_secret: '6UBvXZuLqTGra2uYEPIqREtlmf2bXpUucMAGWqQhZ5Jn8EU5Ly',
    access_token_key: '86738676-nEM30xDEERV8J5qmn5G31rrISkjGOfaWtkWQWLaiy',
    access_token_secret: 'mGfZmo1NAbu9iNthttmD47piYSnpt7D6uvUKzf5BKkoaT',
  });

  client.get('search/tweets', {q: req.query.q, count: '15'}, function(error, tweets, response){
    if(tweets.statuses.length > 0)
      res.send(jade.renderFile(path.join(__dirname, '../views/twitterResults.jade'), {tweets: tweets.statuses}));
    else
      res.send(jade.renderFile(path.join(__dirname, '../views/noResults.jade'), {query: req.query.q}));
  });

});

module.exports = router;
