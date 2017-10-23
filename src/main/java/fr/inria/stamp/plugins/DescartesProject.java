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
   // **********************************************************************
   public static MavenProject getRootProject()
   {
      return(rootProject);
   }

   // **********************************************************************
   public static MavenProject getCurrentMvnProject()
   {
      return(currentMvnProject);
   }

   // **********************************************************************
   public static DescartesProject getCurrentProject()
   {
      DescartesProject theProject = null;

      if (currentProjectIndex >= 0 && currentProjectIndex < cardProjects())
      {
         theProject = dProjects.get(currentProjectIndex);
      }

      return(theProject);
   }

   // **********************************************************************
   public static int cardProjects()
   {
      return(dProjects.size());
   }

   // **********************************************************************
   public static DescartesProject getProjects(int index)
   {
      DescartesProject theProject = null;

      if (index >= 0 && index < cardProjects())
      {
         theProject = dProjects.get(currentProjectIndex);
      }

      return(theProject);
   }

   // **********************************************************************
   public static void appendProjects(DescartesProject aProject)
   {
      dProjects.add(aProject);
      currentProjectIndex = cardProjects() - 1;
   }

   // **********************************************************************
   public static DescartesProject findInProjects(String aName)
   {
      DescartesProject theProject = null;

      for (int i = 0; (i < cardProjects() && theProject == null); i++)
      {
         if (getProjects(i).getMavenProject().getArtifactId() == aName)
         {
            theProject = getProjects(i);
         }
      }

      return(theProject);
   }

   // **********************************************************************
   // ******** methods
   public static void initialize(MavenProject currentProject)
   {
      if (rootProject == null)
      {
         if (currentProject.getParent() != null)
         // no parents means non muilt-module project
         {
            rootProject = currentProject.getParent();
         }
      }
      currentMvnProject = currentProject;
   }


   // **********************************************************************
   // **********************************************************************

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

      // first run instance is the regular pit one
      appendTestRuns(new DescartesRun(this, theOptions));
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
         anotherModule = findInProjects(projectName);
         if (anotherModule != null)
         {
            newRun = new DescartesRun(getTestRuns(0));
            appendTestRuns(newRun);
            // merge options
            newRun.mergeOptions(anotherModule.getTestRuns(0).getPitOptions());
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
   private static MavenProject rootProject = null;
   private static MavenProject currentMvnProject = null;
   private static ArrayList<DescartesProject> dProjects =
      new ArrayList<DescartesProject>();
   private static int currentProjectIndex = -1;

   private MavenProject mvnProject = null;
   private File baseDirParam;
   private PluginServices pluginsParam;
   private Map<String, String> envVarParam;
   private List<DescartesRun> testRuns = null;
}
