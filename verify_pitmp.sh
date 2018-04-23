#!/bin/sh
################################################################################
# compile, install locally and run automatic tests
currentDir="`pwd`"

testName="verify_pitmp"
traceFile="$currentDir/$testName.traces"
compileFile=$currentDir/$testName"_compile.traces"
testDir="$currentDir/test_dir"
runResFile="$testDir/run_all_tests.res"
refResult="$testDir/$testName.ref"
tmpResFile=$testDir/$testName"_res.$$"
tmpDiffFile=$testDir/$testName"_diff.$$"

# clean previous trace files
rm -f $traceFile $compileFile $runResFile $testDir/$testName"_diff".* $testDir/$testName"_res".*

echo "------------------------------------------------------------" 2>&1 | tee $traceFile
echo "- compile and install locally" 2>&1 | tee -a $traceFile
echo "------------------------------------------------------------" 2>&1 | tee -a $traceFile
if mvn clean install >> $compileFile 2>&1
then
   echo "OK" 2>&1 | tee -a $traceFile
   echo "------------------------------------------------------------" 2>&1 | tee -a $traceFile
   echo "- run tests" 2>&1 | tee -a $traceFile
   echo "------------------------------------------------------------" 2>&1 | tee -a $traceFile
   cd $testDir
   ./run_all_tests.sh >$runResFile 2>&1

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
