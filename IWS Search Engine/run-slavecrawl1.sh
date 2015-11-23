#!/bin/bash

java -classpath lib/*:target/WEB-INF/classes/. webserver.HttpServer 8081 /home/cis455/workspace/project/ ./conf/worker1-web.xml
