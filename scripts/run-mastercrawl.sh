#!/bin/bash

java -classpath lib/*:target/WEB-INF/classes/. crawler.webserver.HttpServer 8080 . ./conf/master-web.xml
