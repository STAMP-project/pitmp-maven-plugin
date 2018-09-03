# PitMP: Maven plugin to handle multi module projects for PiTest
[![Build Status](https://travis-ci.org/STAMP-project/pitmp-maven-plugin.svg?branch=master)](https://travis-ci.org/STAMP-project/pitmp-maven-plugin)
[![Coverage Status](https://coveralls.io/repos/github/STAMP-project/pitmp-maven-plugin/badge.svg?branch=master)](https://coveralls.io/github/STAMP-project/pitmp-maven-plugin?branch=master)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/eu.stamp-project/pitmp-maven-plugin/badge.svg?style=flat)](https://maven-badges.herokuapp.com/maven-central/eu.stamp-project/pitmp-maven-plugin)

## Table of contents
  - [Quick start](#quick-start)
  - [What is PitMP?](#what-is-pitmp)
  - [How does PitMP work?](#how-does-pitmp-work)
  - [PitMP Output](#pitmp-output)
  - [Running PitMP on your project](#running-pitmp-on-your-project)
  - [Releases](#releases)
  - [Tested on](#tested-on)


## Quick start
* on command line  
  - Compile your project
  ```
  mvn install
  ```
  - and run PIT
  ```
  mvn eu.stamp-project:pitmp-maven-plugin:run
  ```
  - or run Descartes
  ```
  mvn eu.stamp-project:pitmp-maven-plugin:descartes
  ```

* with Maven  
 Modify your `pom.xml`,
  - for PIT include:
  ```
      <plugin>
        <groupId>eu.stamp-project</groupId>
        <artifactId>pitmp-maven-plugin</artifactId>
        <version>1.3.4</version>
      </plugin>
  ```
  - and for Descartes include:
  ```
      <plugin>
        <groupId>eu.stamp-project</groupId>
        <artifactId>pitmp-maven-plugin</artifactId>
        <version>1.3.4</version>
        <configuration>
          <mutationEngine>descartes</mutationEngine>
        </configuration>
      </plugin>
  ```


## What is PitMP?
PitMP (PIT for Multi-module Project) is a Maven plugin able to run PIT on multi-module projects.
PIT is a mutation testing system for Java applications, which allows you to evaluate
the quality of your test suites.

To know more about PIT: http://pitest.org

## How does PitMP work?

PIT takes a test suite, a set of classes to be mutated and a set of mutation operators
and computes a line coverage and a mutation coverage:    
![PIT inputs and outputs](docs/pit_inputs_outputs.png)

PIT mutates only the classes defined in the same module than the
test suite:    
![PIT project](docs/pit_project.png)

PitMP runs PIT on every test suite, mutating classes of all dependencies of modules
located in the same project tree:    
![PitMP project](docs/pmp_project_1.png)  
then:  
![PitMP project](docs/pmp_project_2.png)  
etc...

PitMP just extends PIT, it doesn't rewrite any feature, so all PIT's properties can
be used.
PitMP runs test suite as PIT does, just extending the list of classes to be
mutated to the whole project tree, instead of mutating only the classes of
the test suite module.


## PitMP Output

PIT produces a report that includes:
* a summary of line coverage and mutation coverage scores:
![PIT summary](docs/pit_summary_dnoo.png)
* a detail report for each class combining line coverage and mutation coverage
information:
![PIT detail](docs/pit_detail_dnoo.png)  
*Light green shows line coverage, dark green shows mutation coverage.*  
*Light pink show lack of line coverage, dark pink shows lack of mutation coverage.*


## Running PitMP on your project

* Go to the project on which you want to apply PIT

* Compile your project
```
mvn install
```
* Run PIT on your multi module project :-)
```
mvn eu.stamp-project:pitmp-maven-plugin:run
```

##### Install the plugin for releases earlier than 1.1.4
Since 1.1.4 PitMP is available on Maven Central, so this step is required only
for releases before 1.1.4.
```
git clone https://github.com/STAMP-project/pitmp-maven-plugin.git
cd pitmp-maven-plugin
mvn install
```

### Configure PitMP

You can configure your project in the root pom.xml, in the section \<plugins\>:
```
  <plugin>
    <groupId>eu.stamp</groupId>
    <artifactId>pitmp-maven-plugin</artifactId>
    <version>release.you.want</version>
    <!-- List all the packages of the project that contain classes you want
         to be mutated.
         All PIT's properties can be used.
    -->
    <configuration>
      <targetClasses>
        <param>a.package.of.classes*</param>
        <param>another.package.of.classes*</param>
      </targetClasses>
    </configuration>
  </plugin>
```

### PitMP properties

* targetModules: to run PIT only on specified modules    
  You can use the property "targetModules" in the pom.xml:
  ```
          <targetModules>
            <param>yourFirstModule</param>
            <param>anotherModule</param>
          </targetModules>
  ```
  or on the command line, use:
  ```
  mvn "-DtargetModules=yourFirstModule,anotherModule" pitmp:run
  ```
  Running PitMP from a module directory will NOT work.
* skippedModules: to run PIT only on specified modules    
  You can use the property "skippedModules" in the pom.xml:
  ```
          <skippedModules>
            <param>aModuleToSkip</param>
            <param>anotherModuleToSkip</param>
          </skippedModules>
  ```
  or on the command line, use:
  ```
  mvn "-DtargetModules=aModuleToSkip,anotherModuleToSkip" pitmp:run
  ```

## Running Descartes

If you want to run Descartes:
```
mvn eu.stamp-project:pitmp-maven-plugin:descartes
```
If you want to configure Descartes, add to your root project pom.xml, in the section \<plugins\>:
```
  <plugin>
    <groupId>eu.stamp</groupId>
    <artifactId>pitmp-maven-plugin</artifactId>
    <version>release.you.want</version>
    <!-- list all the packages of the project that contain classes you want to be mutated    -->
    <configuration>
      <targetClasses>
        <param>a.package.of.classes*</param>
        <param>another.package.of.classes*</param>
      </targetClasses>
      <mutationEngine>descartes</mutationEngine>
    </configuration>
  </plugin>
```

For complete instructions about Descartes see the [Descartes github](https://github.com/STAMP-project/pitest-descartes).

For an example of multi module project using PitMP see the [dnoo github](https://github.com/STAMP-project/dnoo).

### Check Pseudo/Partially Tested Methods number
If you want to check the number of Pseudo Tested Methods and/or Partially Tested Methods are below a specific thresholds add **pseudoTestedThreshold** and **partiallyTestedThreshold** in the configuration:
```
  <plugin>
    <groupId>eu.stamp</groupId>
    <artifactId>pitmp-maven-plugin</artifactId>
    <version>release.you.want</version>
    <configuration>
      <!-- Check Pseudo/Partially Tested Methods -->
      <pseudoTestedThreshold>1</pseudoTestedThreshold>
      <partiallyTestedThreshold>1</partiallyTestedThreshold>
      <targetClasses>
        <param>a.package.of.classes*</param>
        <param>another.package.of.classes*</param>
      </targetClasses>
      <mutationEngine>descartes</mutationEngine>
    </configuration>
  </plugin>
```

## Releases
For PIT release...         | use PitMP release... | how to use PitMP
-------------------------- | -------------------- | ----------------
1.4.0                      | 1.3.4, 1.3.3, 1.3.2, 1.3.1, 1.3.0, 1.2.0 | Maven Central
1.3.2                      | 1.1.6, 1.1.5         | Maven Central
1.3.1                      | 1.1.4                | Maven Central
1.2.1, 1.2.2, 1.2.4, 1.2.5, 1.3.0  | not tested   |
1.2.0, 1.2.3               | 1.0.1                | git clone & mvn install

* pitmp-maven-plugin-1.3.4
  - Dependency on Descartes v1.2.4 (hotfix for a regression in 1.3.3)
  - Tested with PIT v1.4.0, Descartes v1.2.4, JUnit4 and JUnit5

* pitmp-maven-plugin-1.3.3
  - Dependency on Descartes v1.2.3
  - Tested with PIT v1.4.0, Descartes v1.2.3, JUnit4 and JUnit5

* pitmp-maven-plugin-1.3.2
  - Dependency on Descartes v1.2.2
  - Tested with PIT v1.4.0, Descartes v1.2.2, JUnit4 and JUnit5

* pitmp-maven-plugin-1.3.1
  - Dependency on Descartes v1.2.1
  - Tested with PIT v1.4.0 and Descartes v1.2.1

* pitmp-maven-plugin-1.3.0
  - Adding a goal 'descartes'
  - Dependency on Descartes v1.2
  - Tested with PIT v1.4.0 and Descartes v1.2

* pitmp-maven-plugin-1.2.0
  - Tested with PIT v1.4.0 and Descartes v1.2

* pitmp-maven-plugin-1.1.6
  - Tested with PIT v1.3.2
  - Fixed issues:
    - [#10](https://github.com/STAMP-project/pitmp-maven-plugin/issues/10)

* pitmp-maven-plugin-1.1.5
  - Tested with PIT v1.3.2
  - Fixed issues:
    - [#6](https://github.com/STAMP-project/pitmp-maven-plugin/issues/6)
    - [#9](https://github.com/STAMP-project/pitmp-maven-plugin/issues/9)
      (Duplicate [#6](https://github.com/STAMP-project/pitmp-maven-plugin/issues/6))
  - Add automatic tests (verify_pitmp.sh)
  - Limitation: [#10](https://github.com/STAMP-project/pitmp-maven-plugin/issues/10)

* v1.1.0, pitmp-maven-plugin-1.1.4
  - Tested with PIT v1.3.1

* v1.0.1
  - Tested with PIT v1.2.0 and Descartes v0.2-SNAPSHOT
  - Tested with PIT v1.2.3

## Tested on

* Ubuntu 16.04.4 LTS

* [dhell project on github](https://github.com/STAMP-project/dhell)
* [dnoo project on github](https://github.com/STAMP-project/dnoo)
* [xwiki-commons project on github](https://github.com/xwiki/xwiki-commons)
* [xwiki-rendering project on github](https://github.com/xwiki/xwiki-rendering)

Feedbacks are welcome ! :-)
