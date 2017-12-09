PIT Multi-module Project Maven plugin
-------------------------------------
To handle multi-module project for PIT.		

It run a test suite, mutating classes of all dependencies which are a project module.
Project modules are defined here as maven project located in the same tree.

Install the plugin
------------------
```
git clone https://github.com/STAMP-project/pitmp-maven-plugin.git
cd pitmp-maven-plugin
mvn install
```

Running PitMP
-------------
* Go to the project on which you want to apply PIT

* Add to your root project pom.xml, in the \<plugins\> section:
```
  <plugin>
    <groupId>fr.inria.stamp.plugins</groupId>
    <artifactId>pitmp-maven-plugin</artifactId>
    <version>version.you.want</version>
    <!-- you have to list all the package of the project that contain
    -    classes you want to mutate
    -    sorry for that didn't find the way to workaround the behavior
    -    for default values...yet ! :-)
    -->
    <configuration>
      <targetClasses>
        <param>a.package.of.classes.to.mutate*</param>
        <param>another.package.of.classes.to.mutate*</param>
      </targetClasses>
    </configuration>
    <dependencies>
      <dependency>
        <groupId>org.pitest</groupId>
        <artifactId>pitest-maven</artifactId>
        <version>1.2.0</version>
      </dependency>
    </dependencies>
  </plugin>
```
* Compile your project
```
mvn install
```
* Run PIT on your multimodule project :-)
```
mvn pitmp:run
```
* Run PIT on specified modules
You can use the property "targetModules" to run PIT on one or several modules.
On the command line, use:
```
mvn "-DtargetModules=<module1>,<module2>" pitmp:run
```
Running PitMP from a module directory will NOT work.

Running Descartes
-----------------
If you want to run Descartes, add to your root project pom.xml, in the \<plugins\> section:
```
  <plugin>
    <groupId>fr.inria.stamp.plugins</groupId>
    <artifactId>pitmp-maven-plugin</artifactId>
    <version>version.you.want</version>
    <!-- you have to list all the package of the project that contain
    -    classes you want to mutate
    -    sorry for that didn't find the way to workaround the behavior
    -    for default values...yet ! :-)
    -->
    <configuration>
      <targetClasses>
        <param>a.package.of.classes.to.mutate*</param>
        <param>another.package.of.classes.to.mutate*</param>
      </targetClasses>
      <mutationEngine>descartes</mutationEngine>
      <mutators>
        <mutator>void</mutator>
        <mutator>null</mutator>
        <mutator>true</mutator>
        <mutator>false</mutator>
        <mutator>empty</mutator>
        <mutator>0</mutator>
        <mutator>1</mutator>
        <mutator>(byte)0</mutator>
        <mutator>(byte)1</mutator>
        <mutator>(short)1</mutator>
        <mutator>(short)2</mutator>
        <mutator>0L</mutator>
        <mutator>1L</mutator>
        <mutator>0.0</mutator>
        <mutator>1.0</mutator>
        <mutator>0.0f</mutator>
        <mutator>1.0f</mutator>
        <mutator>'\40'</mutator>
        <mutator>'A'</mutator>
        <mutator>""</mutator>
        <mutator>"A"</mutator>
      </mutators>
    </configuration>
    <dependencies>
      <dependency>
        <groupId>org.pitest</groupId>
        <artifactId>pitest-maven</artifactId>
        <version>1.2.0</version>
      </dependency>
      <dependency>
        <groupId>fr.inria.stamp</groupId>
        <artifactId>descartes</artifactId>
        <version>0.2-SNAPSHOT</version>
      </dependency>
    </dependencies>
  </plugin>
```

For complete instructions about Descartes see the [Descartes github](https://github.com/STAMP-project/pitest-descartes).

For an example of multi module project using PitMP se the [dnoo github](https://github.com/STAMP-project/dnoo).

Tested on
----------
* [dhell project on github](https://github.com/STAMP-project/dhell)
* [dnoo project on github](https://github.com/STAMP-project/dnoo)
* [xwiki-rendering project on github](https://github.com/xwiki/xwiki-rendering)
