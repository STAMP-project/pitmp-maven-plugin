#!/bin/sh
################################################################################
fileExtension="pitmp"
pomFile="pom.xml"
pomNameBase="pom.xml.$fileExtension"
confList="noconf conf1"

testResFile="run_tests_"`date +%Y%m%d_%Hh%M`".res"
rm -rf $fileExtension.failed.*

for theConf in $confList
do
   thePom="$pomNameBase.$theConf"
   traceFile="$fileExtension.traces"
   errorFile="$fileExtension.failed.$theConf"

   echo "########################################" >$traceFile 2>&1

   if test -f $thePom
   then
      # silent build
      mvn clean 2>&1 >/dev/null
      cp $thePom $pomFile
      mvn install 2>&1 >/dev/null
   
      tracedDate=`date +%T`
      echo "######## $tracedDate" >>$traceFile 2>&1
      mvn -e pitmp:run >> $traceFile 2>&1
      tracedDate=`date +%T`
      echo "######## $tracedDate" >>$traceFile 2>&1
   
      # check the result, keep the traces file if test failed
      runRes=`grep '\[INFO\] BUILD SUCCESS' $traceFile`
      if test "X$runRes" = "X"
      then
         echo "######## $thePom: FAILED" 2>&1 | tee -a $testResFile
         echo "######## $thePom: FAILED" >> $traceFile 2>&1
         echo "########################################" >> $traceFile 2>&1
         cp $traceFile $errorFile
      else
         echo "######## $thePom: OK" 2>&1 | tee -a $testResFile
         echo "######## $thePom: OK" >> $traceFile 2>&1
         echo "########################################" >> $traceFile 2>&1
      fi
   else
      tracedDate=`date +%T`
      echo "######## $tracedDate" >> $traceFile 2>&1
      echo "######## $thePom: FAILED" 2>&1 | tee -a $testResFile
      echo "######## $thePom: FAILED" >> $traceFile 2>&1
      echo "########################################" >> $traceFile 2>&1
      cp $traceFile $errorFile
   fi

done
