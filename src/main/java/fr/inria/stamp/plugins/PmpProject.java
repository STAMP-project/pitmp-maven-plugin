package fr.inria.stamp.plugins;

// **********************************************************************
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.io.File;

import org.apache.maven.project.MavenProject;
// import org.apache.maven.model.Model;
import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.MojoExecutionException;

import org.pitest.functional.Option;
import org.pitest.mutationtest.config.ReportOptions;
import org.pitest.mutationtest.config.PluginServices;
import org.pitest.mutationtest.tooling.CombinedStatistics;

import org.pitest.maven.AbstractPitMojo;

// **********************************************************************
public class PmpProject
{
   // **********************************************************************
   // public
   // **********************************************************************
   // ******** attributes
   public String getName()
   {
      return(theMojo.getProject().getArtifactId());
   }

   // **********************************************************************
   // ******** associations
   public PmpMojo getTheMojo()
   {
      return(theMojo);
   }

   // **********************************************************************
   public int cardTestRuns()
   {
      return(testRuns.size());
   }

   // **********************************************************************
   public PmpRun getTestRuns(int index)
   {
      PmpRun theRun = null;

      if (index >= 0 && index < cardTestRuns())
      {
         theRun = testRuns.get(index);
      }

      return(theRun);
   }

   // **********************************************************************
   public void appendTestRuns(PmpRun aRun)
   {
      testRuns.add(aRun);
   }

   // **********************************************************************
   // ******** methods
   public PmpProject(PmpMojo mojo)
   {
      theMojo = mojo;
      testRuns = new ArrayList<PmpRun>();

      // first run instance is the regular pit one
      appendTestRuns(new PmpRun(this));

      // System.out.println("# root Id: " + PmpContext.getInstance().getRootProject().getArtifactId());
      // System.out.println("# project Id: " + theMojo.getProject().getArtifactId());
   }

   // **********************************************************************
   public CombinedStatistics execute() throws MojoExecutionException
   {
      System.out.println("################ PmpProject.execute: IN");
      printInfo();

      // create additionnal runs
      generateRuns();

      // run all
      // first run  is the pitest regular one
      for (int i = 0; i < cardTestRuns(); i++)
      {
         getTestRuns(i).execute();
      }

      // merge and consolidate results into the first run
      mergeResults();

      // return first run results
      System.out.println("################ PmpProject.execute: OUT");
      return getTestRuns(0).getResults();
   }

   // **********************************************************************
   public void generateRuns()
   {
      List<Dependency> myDependencies = getTheMojo().getProject().getDependencies();
      String projectName;
      PmpProject targetClassModule;
      PmpRun newRun;

      System.out.println("################ PmpProject.generateRuns: IN");

      for (int i = 0; i < myDependencies.size(); i++)
      {
         projectName = myDependencies.get(i).getArtifactId();
         targetClassModule = PmpContext.getInstance().findInProjects(projectName);
         System.out.println("# projectName = " + projectName + " - targetClassModule = "
            + targetClassModule);
         if (targetClassModule != null)
         {
            newRun = new PmpRun(getTestRuns(0), targetClassModule);
            appendTestRuns(newRun);
         }
      }
      System.out.println("################ PmpProject.generateRuns: OUT");
   }

   // **********************************************************************
   public void mergeResults()
   {
      System.out.println("################ PmpProject.mergeResults");
   }

   // **********************************************************************
   public void printInfo()
   {
      List<Dependency> projectDependencies = getTheMojo().getProject().getDependencies();

      System.out.println("################ project: " +
         getTheMojo().getProject().getArtifactId());
      System.out.println("#");
      System.out.println("# Parent: " + getTheMojo().getProject().getParent()
         .getArtifactId());
      System.out.print("# Dependencies: ");
      for (int i = 0; i < projectDependencies.size(); i++)
      {
         System.out.print(projectDependencies.get(i).getArtifactId() + ", ");
      }
      System.out.println("");
      System.out.println("################");
   }

   // **********************************************************************
   // private
   // **********************************************************************
   // ******** attributes
   private PmpMojo theMojo = null;
   private ArrayList<PmpRun> testRuns;
}
