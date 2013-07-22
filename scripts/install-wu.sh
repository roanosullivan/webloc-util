#!/bin/bash

GROUP=`mvn help:evaluate -Dexpression=project.groupId | grep -v '\['`
ARTIFACT=`mvn help:evaluate -Dexpression=project.artifactId | grep -v '\['`
VERSION=`mvn help:evaluate -Dexpression=project.version | grep -v '\['`

#1) build executable binary
lein uberjar

#2) install binary in maven repo
mvn install:install-file -Dfile=target/$ARTIFACT-$VERSION-standalone.jar -DgroupId=$GROUP -DartifactId=$ARTIFACT -Dversion=$VERSION -Dpackaging=JAR -Dclassifier=standalone

#3) install scripts/wu script in ~/bin
mkdir -p ~/bin
cp scripts/wu ~/bin/

#4) DONE!
echo "Put ~/bin is on your path to invoke 'wu' from any dir"
