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
public class DescartesProject
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
   public AbstractPitMojo getTheMojo()
   {
      return(theMojo);
   }

   // **********************************************************************
   public int cardTestRuns()
   {
      return(testRuns.size());
   }

   // **********************************************************************
   public DescartesRun getTestRuns(int index)
   {
      DescartesRun theRun = null;

      if (index >= 0 && index < cardTestRuns())
      {
         theRun = testRuns.get(index);
      }

      return(theRun);
   }

   // **********************************************************************
   public void appendTestRuns(DescartesRun aRun)
   {
      testRuns.add(aRun);
   }

   // **********************************************************************
   // ******** methods
   public DescartesProject(AbstractPitMojo mojo)
   {
      theMojo = mojo;
      testRuns = new ArrayList<DescartesRun>();

      // first run instance is the regular pit one
      appendTestRuns(new DescartesRun(this));

      // System.out.println("# root Id: " + DescartesContext.getInstance().getRootProject().getArtifactId());
      // System.out.println("# project Id: " + theMojo.getProject().getArtifactId());
   }

   // **********************************************************************
   public CombinedStatistics execute() throws MojoExecutionException
   {
      System.out.println("################ DescartesProject.execute: IN");
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
      System.out.println("################ DescartesProject.execute: OUT");
      return getTestRuns(0).getResults();
   }

   // **********************************************************************
   public void generateRuns()
   {
      List<Dependency> myDependencies = getTheMojo().getProject().getDependencies();
      String projectName;
      DescartesProject targetClassModule;
      DescartesRun newRun;

      System.out.println("################ DescartesProject.generateRuns: IN");

      for (int i = 0; i < myDependencies.size(); i++)
      {
         projectName = myDependencies.get(i).getArtifactId();
         targetClassModule = DescartesContext.getInstance().findInProjects(projectName);
         System.out.println("# projectName = " + projectName + " - targetClassModule = "
            + targetClassModule);
         if (targetClassModule != null)
         {
            newRun = new DescartesRun(getTestRuns(0), targetClassModule);
            appendTestRuns(newRun);
         }
      }
      System.out.println("################ DescartesProject.generateRuns: OUT");
   }

   // **********************************************************************
   public void mergeResults()
   {
      System.out.println("################ DescartesProject.mergeResults");
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
   private AbstractPitMojo theMojo = null;
   private ArrayList<DescartesRun> testRuns;
}
