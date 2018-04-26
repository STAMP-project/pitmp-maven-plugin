#!/bin/sh
################################################################################
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

fileExtension="pitmp"
pomFile="pom.xml"
pomNameBase="$pomFile.$fileExtension"
defaultPom="$pomNameBase.noconf.template"
confList="noconf conf1"

if test "$execOption" = "run"
then
   if test "X$pitmpVersion" = "X"
   then
      pitmpVersion=`grep '<version>' ../../$pomFile | head -n 1 | \
         sed -e "s/ *<version>//" -e "s/<\/version>//"`
   fi
   
   testResFile="run_tests_"`date +%Y%m%d_%Hh%M`".res"
   rm -rf $fileExtension.failed.*
   
   for theConf in $confList
   do
      thePom="$pomNameBase.$theConf"
      pomTemplate="$thePom.template"
      traceFile="$fileExtension.traces"
      errorFile="$fileExtension.failed.$theConf"
   
      echo "########################################" >$traceFile 2>&1
   
      if test -f $pomTemplate
      then
         # generate pom file
         sed -e "s/##PITMP_VERSION##/1.1.6-SNAPSHOT/" $pomTemplate > $pomFile
   
         # silent build
         mvn clean install 2>&1 >/dev/null
      
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
         echo "######## $thePom: ERROR (missing $pomTemplate)" 2>&1 | tee -a $testResFile
         echo "######## $thePom: ERROR (missing $pomTemplate)" >> $traceFile 2>&1
         echo "########################################" >> $traceFile 2>&1
         cp $traceFile $errorFile
      fi
   done
else
   # generate pom file
   sed -e "s/##PITMP_VERSION##/1.1.6-SNAPSHOT/" $defaultPom > $pomFile
   mvn clean

   rm -f $fileExtension.failed.* run_tests_*.res pom.xml
fi
