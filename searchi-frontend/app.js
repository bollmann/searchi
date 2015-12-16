var express = require('express');
var path = require('path');
var favicon = require('serve-favicon');
var logger = require('morgan');
var cookieParser = require('cookie-parser');
var bodyParser = require('body-parser');

var index = require('./routes/index');
var search = require('./routes/search');
var results = require('./routes/results');
var twitter = require('./routes/twitter');
var amazon = require('./routes/amazon');
var amazonImg = require('./routes/amazonImg');
var weather = require('./routes/weather')
var snippet = require('./routes/snippet')
var searchImages = require('./routes/searchImages');
var app = express();

// view engine setup
app.set('views', path.join(__dirname, 'views'));
app.set('view engine', 'jade');

console.log('starting up')

// uncomment after placing your favicon in /public
app.use(favicon('public/favicon.ico'));
app.use(logger('dev'));
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: false }));
app.use(cookieParser());
app.use(express.static(path.join(__dirname, 'public')));

app.use('/', index);
app.use('/search', search);
app.use('/results', results);
app.use('/twitter', twitter);
app.use('/amazon', amazon);
app.use('/amazonImg', amazonImg);
app.use('/weather', weather);
app.use('/snippet', snippet);
app.use('/searchImages', searchImages);

// catch 404 and forward to error handler
app.use(function(req, res, next) {
  var err = new Error('Not Found');
  err.status = 404;
  next(err);
});

// error handlers

// development error handler
// will print stacktrace
if (app.get('env') === 'development') {
  app.use(function(err, req, res, next) {
    res.status(err.status || 500);
    res.render('error', {
      url: req.url
    });
  });
}

// production error handler
// no stacktraces leaked to user
app.use(function(err, req, res, next) {
  res.status(err.status || 500);
  res.render('error', {
    url: req.url
  });
});


module.exports = app;
