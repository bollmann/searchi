#!/bin/bash
echo "Killing java process"
ps -aef | grep java | head -n 1 | sed -e 's/\s\{1,\}/ /g' | cut -f2 -d" " | xargs kill -9
echo "Restarting worker"
pushd ~/searchi
/usr/bin/nohup bash ~/searchi/scripts/run-slavecrawl.sh > crawl.out 2>&1 &
echo "Restarted worker"
pwd
