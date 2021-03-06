#!/bin/bash
set -e
set -x

source $TESTROOTDIR/tests_lib

fury layer init
fury project add -n policy-file-test
fury repo add -u https://github.com/propensive/base.git -n base
fury import add -i base:2.12.6

# Try writing inside shared dir
fury module add -n write-inside-shared
fury source add -d src
fury module update -c scala/compiler
fury module update --type application
fury module update --main test.WriteInsideSharedDir
fury


# Try writing outside shared dir
fury module add -n write-outside-shared
fury source add -d src
fury module update -c scala/compiler
fury module update --type application
fury module update --main test.WriteOutsideSharedDir
! fury # Expected failure - permission denied
