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

* Add to your project pom.xml:
```
  <plugin>
    <groupId>fr.inria.stamp.plugins</groupId>
    <artifactId>pitmp-maven-plugin</artifactId>
    <version>0.1.0</version>
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

For complete instructions about Descartes see the Descartes github.
