#!/bin/sh
################################################################################
printUsage()
{
   echo "Usage: $ScriptName [-h | -d <directory> [<options>]]"
}

################################################################################
help()
{
   printUsage

   echo
   echo "Run automatic tests using $testScriptName, in all test directories"
   echo "located in the current directory."
   echo
   echo "Options:"
   echo "-h: this help. Run '$testScriptName -h' for other options."
   echo "-d <directory>: by default all tests are executed, this option executes"
   echo "    only tests in the <directory>. Available directories are: $dirList"
   echo "<options>: other options passed to '$testScriptName'."

   exit 0
}

################################################################################
usage()
{
   printUsage
   exit 1
}

################################################################################
# commandline analysis

ScriptName=`basename "$0"`
currentDir=`pwd`

testScriptName="run_tests.sh"
testScript="$currentDir/$testScriptName"
dirList="dhell dnoo"

execOption="run"
runTestOptions=
testDirList=
while test ! "X$1" = "X"
do
   if test "$1" = "-h"
   then
      help
   elif test "$1" = "-d"
   then
      if test "X$2" = "X"
      then
         echo "Error: -d option requires an argument"
         usage
      else
         testDirList="$testDirList $2"
         shift
      fi
   else
      runTestOptions="$runTestOptions $1"
   fi
   shift
done

################################################################################

if test "X$testDirList" = "X"
then
   testDirList="$dirList"
fi

for theDir in $testDirList
do
   cd $currentDir
   cd $theDir
   echo "################ $theDir"
   $testScript $runTestOptions
done
