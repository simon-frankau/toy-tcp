#!/bin/sh

# Script to start the Java implementation of ToyTCP. Should be an
# argument to pppd.

set -e
cd $(dirname $0)/out/production/toytcp
# TODO: Need to find a sensible way to get the classpath working
export CLASSPATH=.:/Users/sgf/.m2/repository/org/slf4j/slf4j-api/1.7.10/slf4j-api-1.7.10.jar:/Users/sgf/.m2/repository/org/slf4j/slf4j-simple/1.7.10/slf4j-simple-1.7.10.jar
exec java name/arbitrary/toytcp/Main 2> ../../../toytcp.log
