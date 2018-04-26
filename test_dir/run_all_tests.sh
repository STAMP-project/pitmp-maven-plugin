#!/bin/sh
################################################################################
currentDir=`pwd`
options="$*"
testScript="$currentDir/run_tests.sh"

dirList="dhell dnoo"

for theDir in $dirList
do
   cd $currentDir
   cd $theDir
   echo "################ $theDir"
   $testScript $options
done
