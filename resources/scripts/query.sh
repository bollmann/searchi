#!/bin/sh

cd ../ # go to searchi home directory
java -cp "lib/*:bin/" indexer.InvertedIndex query $@
