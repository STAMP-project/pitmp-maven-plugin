#!/bin/sh
################################################################################
# compile, install locally and run automatic tests
# or clean all test files

currentDir="`pwd`"
execOption="run"
pitmpVersion=
if test "X$1" != "X"
then
   if test "$1" = "-clean"
   then
      execOption="clean"
   else
      pitmpVersion="$1"
   fi
fi

testName="verify_pitmp"
traceFile="$currentDir/$testName.traces"
compileFile=$currentDir/$testName"_compile.traces"
testDir="$currentDir/test_dir"
runResFile="$testDir/run_all_tests.res"
refResult="$testDir/$testName.ref"
tmpResFile=$testDir/$testName"_res.$$"
tmpDiffFile=$testDir/$testName"_diff.$$"
pomFile="pom.xml"

# clean previous trace files
rm -f $traceFile $compileFile $runResFile $testDir/$testName"_diff".* $testDir/$testName"_res".*

if test "$execOption" = "run"
then
   # get version to test
   if test "X$pitmpVersion" = "X"
   then
      pitmpVersion=`grep '<version>' $pomFile | head -n 1 | \
         sed -e "s/ *<version>//" -e "s/<\/version>//"`
   fi
   
   echo "------------------------------------------------------------" 2>&1 | tee $traceFile
   echo "- testing version: $pitmpVersion" 2>&1 | tee -a $traceFile
   echo "------------------------------------------------------------" 2>&1 | tee -a $traceFile
   echo "- compile and install locally" 2>&1 | tee -a $traceFile
   echo "------------------------------------------------------------" 2>&1 | tee -a $traceFile
   if mvn clean install >> $compileFile 2>&1
   then
      echo "OK" 2>&1 | tee -a $traceFile
      echo "------------------------------------------------------------" 2>&1 | tee -a $traceFile
      echo "- run tests" 2>&1 | tee -a $traceFile
      echo "------------------------------------------------------------" 2>&1 | tee -a $traceFile
      cd $testDir
      ./run_all_tests.sh $pitmpVersion >$runResFile 2>&1
   
      # check result
      grep "^########" $runResFile > $tmpResFile
      if diff $tmpResFile $refResult >$tmpDiffFile
      then
         echo "OK" 2>&1 | tee -a $traceFile
         # clean all traces files
         rm -f $traceFile $compileFile $runResFile $testDir/$testName"_diff".* $testDir/$testName"_res".*
         exit 0
      else
         echo "FAILED" 2>&1 | tee -a $traceFile
         echo "-------- results" 2>&1 | tee -a $traceFile
         cat "$tmpResFile" 2>&1 | tee -a $traceFile
         echo "-------- diff" 2>&1 | tee -a $traceFile
         cat "$tmpDiffFile" 2>&1 | tee -a $traceFile
         # remove trace files except the main one
         rm -f $compileFile $runResFile $testDir/$testName"_diff".* $testDir/$testName"_res".*
         exit 1
      fi
   else
      echo "FAILED" 2>&1 | tee -a $traceFile
      echo "-------- traces" 2>&1 | tee -a $traceFile
      cat "$compileFile" 2>&1 | tee -a $traceFile
      # remove trace files except the main one
      rm -f $compileFile $runResFile $testDir/$testName"_diff".* $testDir/$testName"_res".*
      exit 1
   fi
else
   cd $testDir
   ./run_all_tests.sh -clean
fi
