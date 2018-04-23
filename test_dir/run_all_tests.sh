#!/bin/sh
################################################################################
currentDir=`pwd`
testScript="$currentDir/run_tests.sh"

dirList="dhell dnoo"

for theDir in $dirList
do
   cd $currentDir
   cd $theDir
   echo "################ $theDir"
   $testScript
done
