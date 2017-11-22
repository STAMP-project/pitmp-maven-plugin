PiTest Multi-module Project Maven plugin
-----------------------------------------
To handle multi-module project for PiTest

Coming soon. :-)


Install the plugin
------------------
```
git clone https://github.com/STAMP-project/pitmp-maven-plugin.git
cd pitmp-maven-plugin
mvn install
```

Running PitMP
-------------
* Go to the project on which you want to apply PiTest

* Add to your root project pom.xml, in the \<plugins\> section:
```
  <plugin>
    <groupId>fr.inria.stamp.plugins</groupId>
    <artifactId>pitmp-maven-plugin</artifactId>
    <version>version.you.want</version>
    <!-- you have to list all the package of the project that contain
    #### classes you want to mutate
    #### sorry for that didn't find the way to workaround the behavior
    #### for default values...yet ! :-)
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
* Run PiTest on your multimodule project :-)
```
mvn pitmp:run
```

Running Descartes
-----------------
If you want to run Descartes, use the regular PiTest tag in the pom.xml file:
```
<mutationEngine>descartes</mutationEngine>
```

For complete instructions about Descartes see the [Descartes github](https://github.com/STAMP-project/pitest-descartes).
