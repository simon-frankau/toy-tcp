#!/bin/sh

# Script to start the Java implementation of ToyTCP. Should be an
# argument to pppd.

set -e
cd $(dirname $0)/out/production/toytcp
exec java name/arbitrary/toytcp/Main
