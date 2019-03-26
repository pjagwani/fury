#!/bin/bash
set -e
set -x

source $TESTROOTDIR/tests_lib

fury layer init
fury project add -n scala
fury module add -n compiler
fury module update -t compiler -C org.scala-lang:scala-compiler:2.12.8
fury project add -n webpage
fury module add -n hello-world
fury source add -d wrong_src
fury undo
fury source add -d src
fury source list
fury module update -c scala/compiler
fury
fury build save --dir ./

OUTPUT=$(java -cp "$SCALA:webpage-hello-world.jar" "HelloWorld")
EXPECTED="Hello, world!"

if [ "$OUTPUT" !=  "$EXPECTED" ]; then
    echo "ERROR: '$OUTPUT' != '$EXPECTED'"
    exit 1
fi
