#!/bin/bash

# Pre:
# $ lein uberjar
# $ cp target/webloc-util-0.1.0-SNAPSHOT-standalone.jar dist/

# Usage:
# $ ./scripts/webloc-util.sh "markdown" `cd ~; pwd`/Desktop

VERSION=0.1.0-SNAPSHOT

java -jar dist/webloc-util-$VERSION-standalone.jar $1 $2
