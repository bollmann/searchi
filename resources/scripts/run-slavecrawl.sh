#!/bin/bash

java -classpath lib/*:bin/. crawler.webserver.HttpServer 8081 . ./conf/worker-web.xml
