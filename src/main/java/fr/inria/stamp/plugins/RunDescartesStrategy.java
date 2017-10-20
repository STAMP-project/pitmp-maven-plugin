package fr.inria.stamp.plugins;

// **********************************************************************
import java.io.File;
import java.util.Map;
import java.util.ArrayList;

import org.apache.maven.project.MavenProject;
import org.apache.maven.plugin.MojoExecutionException;
import org.pitest.mutationtest.config.PluginServices;
import org.pitest.mutationtest.config.ReportOptions;
import org.pitest.mutationtest.tooling.AnalysisResult;
import org.pitest.mutationtest.tooling.CombinedStatistics;
import org.pitest.mutationtest.tooling.EntryPoint;

import org.pitest.maven.GoalStrategy;

// **********************************************************************
public class RunDescartesStrategy implements GoalStrategy
{
   // **********************************************************************
   // public
   // **********************************************************************
   // ******** attributes
   public static MavenProject rootProject = null;
   public static ArrayList<MavenProject> projectList = new ArrayList<MavenProject>();
   public static ArrayList<ReportOptions> projectData = new ArrayList<ReportOptions>();
   public static int currentProject = -1;

   // ******** methods
   @Override
   public CombinedStatistics execute(File baseDir, ReportOptions data,
         PluginServices plugins, Map<String, String> environmentVariables)
      throws MojoExecutionException
   {
      System.out.println("################ RunDescartesStrategy: " + baseDir);
      System.out.println("# baseDir: " + baseDir);
      System.out.println("# rootProject: " + rootProject.getArtifactId());
      System.out.println("# plugins: " + plugins);
      System.out.println("# environmentVariables: " + environmentVariables);
      System.out.println("################");
      printInfo();

      EntryPoint e = new EntryPoint();
      AnalysisResult result = e.execute(baseDir, data, plugins,
            environmentVariables);
      if (result.getError().hasSome())
      {
         throw new MojoExecutionException("fail", result.getError().value());
      }

      return result.getStatistics().value();
   }

   // **********************************************************************
   public void printInfo()
   {
      System.out.println("################################ Info");
      System.out.println("# projects count: " + projectList.size());
      System.out.println("# current project: " + currentProject);
      for (int i = 0; i < projectList.size(); i++)
      {
         printProjectInfo(projectList.get(i), projectData.get(i));
      }
      System.out.println("################################");

   }

   // **********************************************************************
   public void printProjectInfo(MavenProject aProject, ReportOptions projectData)
   {
      System.out.println("################ project: " + aProject.getArtifactId());
      System.out.println("#");
      System.out.println("# targetTests: " + projectData.getTargetTests());
      System.out.println("# targetClasses: " + projectData.getTargetClasses());
      System.out.println("# codePaths: " + projectData.getCodePaths());
      System.out.println("# sourceDirs: " + projectData.getSourceDirs());
      System.out.println("# classPathElements: " + projectData.getClassPathElements());
      System.out.println("# mutationEngine: " + projectData.getMutationEngine());
      System.out.println("################");

   }
}
