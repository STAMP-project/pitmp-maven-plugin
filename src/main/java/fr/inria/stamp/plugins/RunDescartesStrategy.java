package fr.inria.stamp.plugins;

// **********************************************************************
import java.io.File;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.project.MavenProject;
import org.apache.maven.model.Model;
import org.apache.maven.model.Dependency;
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
   // ******** methods
   @Override
   public CombinedStatistics execute(File baseDir, ReportOptions data,
         PluginServices plugins, Map<String, String> environmentVariables)
      throws MojoExecutionException
   {
      CombinedStatistics result = null;

      System.out.println("################################################################");
      System.out.println("# RunDescartesStrategy.execute");
      System.out.println("# rootProject: " +
         DescartesContext.getInstance().getRootProject().getArtifactId());
      System.out.println("# currentProject: " +
         DescartesContext.getInstance().getCurrentMvnProject().getArtifactId());

      DescartesContext.getInstance().appendProjects(new DescartesProject
         (DescartesContext.getInstance().getCurrentMvnProject(), data, baseDir, plugins,
          environmentVariables));
      // DescartesContext.getInstance().printInfo(false);

      result = DescartesContext.getInstance().getCurrentProject().execute();
      System.out.println("################################################################");
      return result;
   }
}
