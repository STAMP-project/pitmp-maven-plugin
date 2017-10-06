descartes-maven-plugin

Descartes Maven plugin
----------------------
To handle multimodule project for Pitest-Descartes

Being developed, not ready yet . :-)


Install the plugin
------------------
  git clone https://github.com/STAMP-project/descartes-maven-plugin.git
  mvn install


Running Desscartes
------------------
* Go to the project on which you want to apply Descartes

* Add to your project pom.xml:
  <plugin>
    <groupId>fr.inria.stamp.plugins</groupId>
    <artifactId>descartes-maven-plugin</artifactId>
    <version>0.1.0</version>
  </plugin>

* Run Descartes
  mvn descartes:run
