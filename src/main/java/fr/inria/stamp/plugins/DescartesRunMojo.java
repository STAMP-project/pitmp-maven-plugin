package fr.inria.stamp.plugins;

// **********************************************************************
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;

import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.model.Model;
import org.apache.maven.model.DependencyManagement;
import org.apache.maven.model.Dependency;

import java.util.*;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

// **********************************************************************
// @Mojo(name = "run", requiresDependencyResolution = ResolutionScope.TEST)
@Mojo(name = "run")
public class DescartesRunMojo extends AbstractMojo
{
   // **********************************************************************
   // public
   // **********************************************************************
   // ******** attributes
   @Parameter(defaultValue = "${project.build.directory}",
      property = "outputDir", required = true)
   private File outputDirectory;

   String OutputFileName;

   // **********************************************************************
   // ******** methods
   DescartesRunMojo()
   {
      OutputFileName = "descartes.results";
   }

   // **********************************************************************
   // org.apache.maven.plugin.MojoExecutionException
   //    if an unexpected problem occurs: ==> "BUILD ERROR"
   // org.apache.maven.plugin.MojoFailureException
   //    if an expected problem: ==> "BUILD FAILURE"
   public void execute() throws MojoExecutionException
   {
      getLog().info("Hello, I'll run Descartes ASAP. :-)");

      createOutputDir();

      readDependencies(".", "       ");
   }

   // **********************************************************************
   // private
   // **********************************************************************
   // ******** attributes
   Model MyProjectModel;

   // **********************************************************************
   // ******** methods
   public void createOutputDir() throws MojoExecutionException
   {
      if (! outputDirectory.exists())
      {
         outputDirectory.mkdirs();
      }

      File resultFile = new File(outputDirectory, OutputFileName);

      FileWriter myWriter = null;
      try
      {
         myWriter = new FileWriter(resultFile);
         myWriter.write(OutputFileName);
      }
      catch (IOException e)
      {
         throw new MojoExecutionException("Error creating file " + resultFile, e);
      }
      finally
      {
         if (myWriter != null)
         {
            try
            {
               myWriter.close();
            }
            catch (IOException e)
            {
               // ignore
            }
         }
      }
   }

   // **********************************************************************
   public void readDependencies(String pomDir, String indentation)
      throws MojoExecutionException
   {
      MavenXpp3Reader mavenReader = new MavenXpp3Reader();
      List<Dependency> theDependencies;
      List<String> theModules;
      File pomFile = new File(pomDir + "/pom.xml");
      String nextIndentation = indentation + "   ";
      DependencyManagement dependencyManager = null;

      if (pomFile.exists())
      {
      try
      {
         MyProjectModel = mavenReader.read(new FileReader(pomFile));
      }
      catch (Exception e)
      {
         throw new MojoExecutionException
            ("ReadDependencies phase: Error reading file " + pomFile, e);
      }

      dependencyManager = MyProjectModel.getDependencyManagement();
      if (dependencyManager != null)
      {
         theDependencies = dependencyManager.getDependencies();
         System.out.println(indentation + "DependencyManagement dependencies (" +
            theDependencies.size() + "): ");
         for (Dependency aDependency: theDependencies)
         {          
            printDependency(indentation, aDependency);
         }
      }

      theDependencies = MyProjectModel.getDependencies();
      System.out.println(indentation + "Dependencies (" + theDependencies.size() +
         "): ");
      for (Dependency aDependency: theDependencies)
      {          
         printDependency(indentation, aDependency);
      }

      theModules = MyProjectModel.getModules();
      System.out.println(indentation + "Modules (" + theModules.size() + "): ");
      for (String aModule: theModules)
      {          
         System.out.println(indentation + "# " + aModule);
         readDependencies(aModule, nextIndentation);
      }
      }
      else
      {
         System.out.println(indentation + "#skipping descartes: no pom.xml");
      }
   }

   // **********************************************************************
   public void printDependency(String indentation, Dependency aDependency)
   {
      System.out.println(indentation + "# " + aDependency.getArtifactId() + " (scope: " + 
         aDependency.getScope() + ")");
   }
}
