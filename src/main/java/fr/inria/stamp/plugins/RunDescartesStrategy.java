package fr.inria.stamp.plugins;

// **********************************************************************
import java.io.File;
import java.util.Map;

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
      System.out.println("################ RunDescartesStrategy: " + baseDir);
      System.out.println("# baseDir: " + baseDir);
      System.out.println("########################################");

      EntryPoint e = new EntryPoint();
      AnalysisResult result = e.execute(baseDir, data, plugins,
            environmentVariables);
      if (result.getError().hasSome())
      {
         throw new MojoExecutionException("fail", result.getError().value());
      }

      return result.getStatistics().value();
   }
}
