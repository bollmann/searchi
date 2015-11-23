#!/bin/bash

java -classpath lib/*:target/WEB-INF/classes/. webserver.HttpServer 8082 /home/cis455/workspace/project/ ./conf/worker2-web.xml
