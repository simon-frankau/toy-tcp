#!/bin/sh

# Runs pppd with our toy TCP implementation scripted inside it.

set -e
cd $(dirname $0)

sudo pppd pty $(pwd)/runtoy.sh
exec tail -f out/production/toytcp/toytcp.log
