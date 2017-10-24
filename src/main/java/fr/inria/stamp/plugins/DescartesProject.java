package fr.inria.stamp.plugins;

// **********************************************************************
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.io.File;

import org.apache.maven.project.MavenProject;
import org.apache.maven.model.Model;
import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.MojoExecutionException;

import org.pitest.functional.Option;
import org.pitest.mutationtest.config.ReportOptions;
import org.pitest.mutationtest.config.PluginServices;
import org.pitest.mutationtest.tooling.CombinedStatistics;

// **********************************************************************
public class DescartesProject
{
   // **********************************************************************
   // public
   // **********************************************************************
   // ******** attributes
   public String getName()
   {
      return(mvnProject.getArtifactId());
   }

   // **********************************************************************
   public File getBaseDirParam()
   {
      return(baseDirParam);
   }

   // **********************************************************************
   public PluginServices getPluginsParam()
   {
      return(pluginsParam);
   }

   // **********************************************************************
   public Map<String, String> getEnvVarParam()
   {
      return(envVarParam);
   }

   // ******** associations
   public MavenProject getMavenProject()
   {
      return(mvnProject);
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
   public DescartesProject(MavenProject theProject, ReportOptions theOptions,
      File baseDir, PluginServices plugins, Map<String, String> environmentVariables)
   {
      mvnProject = theProject;
      baseDirParam = baseDir;
      pluginsParam = plugins;
      envVarParam = environmentVariables;
      testRuns = new ArrayList<DescartesRun>();

      System.out.println("# DescartesProject.DescartesProject");
      // first run instance is the regular pit one
      appendTestRuns(new DescartesRun(this, theOptions));

      System.out.println("# root Id: " + DescartesContext.getInstance().getRootProject().getArtifactId());
      System.out.println("# project Id: " + mvnProject.getArtifactId());
   }

   // **********************************************************************
   public CombinedStatistics execute() throws MojoExecutionException
   {
      System.out.println("################ DescartesProject.execute");
      printInfo();

      // create additionnal runs
      generateRuns();

      // run all
      for (int i = 0; i < cardTestRuns(); i++)
      {
         getTestRuns(i).execute();
      }

      // merge and consolidate results into the first run
      mergeResults();

      // return first run results
      return getTestRuns(0).getResults();
   }

   // **********************************************************************
   public void generateRuns()
   {
      List<Dependency> myDependencies = getMavenProject().getDependencies();
      String projectName;
      DescartesProject anotherModule;
      DescartesRun newRun;

      System.out.println("################ DescartesProject.generateRuns");

      for (int i = 0; i < myDependencies.size(); i++)
      {
         projectName = myDependencies.get(i).getArtifactId();
         anotherModule = DescartesContext.getInstance().findInProjects(projectName);
         System.out.println("# projectName = " + projectName + " - anotherModule = "
            + anotherModule);
         if (anotherModule != null)
         {
            newRun = new DescartesRun(getTestRuns(0),
               anotherModule.getTestRuns(0).getPitOptions());
            appendTestRuns(newRun);
         }
      }
   }

   // **********************************************************************
   public void mergeResults()
   {
      System.out.println("################ DescartesProject.mergeResults");
   }

   // **********************************************************************
   public void printInfo()
   {
      // List<MavenProject> collectedProjects = getMavenProject().getCollectedProjects();
      List<Dependency> projectDependencies = getMavenProject().getDependencies();

      System.out.println("################ project: " +
         getMavenProject().getArtifactId());
      System.out.println("#");
      System.out.println("# Parent: " + getMavenProject().getParent().getArtifactId());
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
   private MavenProject mvnProject = null;
   private File baseDirParam;
   private PluginServices pluginsParam;
   private Map<String, String> envVarParam;
   private List<DescartesRun> testRuns = null;
}
