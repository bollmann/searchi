#!/bin/bash

java -classpath lib/*:bin/. crawler.webserver.HttpServer 8080 . ./conf/master-web.xml
