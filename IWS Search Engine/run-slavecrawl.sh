#!/bin/bash

java -classpath lib/*:target/WEB-INF/classes/. webserver.HttpServer 8080 . ./conf/worker-web.xml
