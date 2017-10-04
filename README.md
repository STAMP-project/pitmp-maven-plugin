descartes-maven-plugin

Maven plugin to handle multimodule project for Pitest-Descartes

1. mvn install

2. Got to the project where you want to apply Descartes

3. add to your project pom.xml:

      <plugin>
        <groupId>fr.inria.stamp.plugins</groupId>
        <artifactId>descartes-maven-plugin</artifactId>
        <version>0.1.0</version>
      </plugin>

4. Run Descartes
   mvn descartes:descartes

