descartes-maven-plugin

Descartes Maven plugin
----------------------
To handle multimodule project for Pitest-Descartes
Being developed, not ready yet . :-)


Running Desscartes
------------------
* Got to the project where you want to apply Descartes

* add to your project pom.xml:

      <plugin>
        <groupId>fr.inria.stamp.plugins</groupId>
        <artifactId>stamp-maven-plugin</artifactId>
        <version>0.1.0</version>
      </plugin>

* Run Descartes: mvn descartes:run

