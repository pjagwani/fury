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
fury source add -d src
fury source list
fury module update --compiler scala/compiler
fury module update --type application
fury module update --main HelloWorld
fury

# Test JAR file validity
fury build save --dir ./

# Run java directly
OUTPUT=$(java -cp "$SCALA:webpage-hello-world.jar" "HelloWorld")
EXPECTED="Hello, world!"
assert_equal "$EXPECTED" "$OUTPUT"

# Test running application module via fury
OUTPUT=$(fury | grep Hello)
EXPECTED="Hello, world!"
assert_equal "$EXPECTED" "$OUTPUT"

# Test native-image file validity
export PATH="${GRAAL_HOME}/bin:${PATH}"
fury restart
mkdir -p native
fury build native --dir native

OUTPUT=$(./native/helloworld)
EXPECTED="Hello, world!"
assert_equal "$EXPECTED" "$OUTPUT"
