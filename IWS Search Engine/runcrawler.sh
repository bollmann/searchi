#!/bin/bash

java -classpath lib/*:target/WEB-INF/classes/. crawler.XPathCrawler 2 10000 yes
#java -classpath lib/*:target/WEB-INF/classes/. edu.upenn.cis455.crawler.XPathCrawler https://en.wikipedia.org resources/wiki/BDBStore 100
