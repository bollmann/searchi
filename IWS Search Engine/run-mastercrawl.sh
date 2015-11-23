#!/bin/bash

java -classpath lib/*:target/WEB-INF/classes/. webserver.HttpServer 8080 /home/cis455/workspace/project/ ./conf/master-web.xml
