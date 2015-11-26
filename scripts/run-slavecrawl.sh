#!/bin/bash

java -classpath lib/*:target/WEB-INF/classes/. crawler.webserver.HttpServer 8081 . ./conf/worker-web.xml
