package fr.inria.stamp.plugins;

// **********************************************************************
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Properties;
import java.io.File;

import org.apache.maven.project.MavenProject;
import org.apache.maven.model.Model;
import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.MojoExecutionException;

import org.pitest.functional.Option;
import org.pitest.functional.predicate.Predicate;
import org.pitest.mutationtest.config.ReportOptions;
import org.pitest.mutationtest.config.PluginServices;
import org.pitest.mutationtest.tooling.CombinedStatistics;
import org.pitest.mutationtest.tooling.EntryPoint;
import org.pitest.mutationtest.tooling.AnalysisResult;
import org.pitest.testapi.TestGroupConfig;

// **********************************************************************
public class DescartesRun
{
   // **********************************************************************
   // public
   // **********************************************************************
   // ******** associations
   public DescartesProject getParent()
   {
      return(dParent);
   }

   // **********************************************************************
   public MavenProject getMavenProject()
   {
      return(getParent().getMavenProject());
   }

   // **********************************************************************
   public File getBaseDir()
   {
      return(getParent().getBaseDirParam());
   }

   // **********************************************************************
   public PluginServices getPlugins()
   {
      return(getParent().getPluginsParam());
   }

   // **********************************************************************
   public Map<String, String> getEnvironmentVariables()
   {
      return(getParent().getEnvVarParam());
   }

   // **********************************************************************
   public ReportOptions getPitOptions()
   {
      return(pitOptions);
   }

   // **********************************************************************
   public CombinedStatistics getResults()
   {
      return(results);
   }

   // **********************************************************************
   // ******** methods
   public DescartesRun(DescartesProject theParent, ReportOptions theOptions)
   {
      dParent = theParent;
      pitOptions = theOptions;
   }

   // **********************************************************************
   public DescartesRun(DescartesRun sourceRun)
   {
      dParent = sourceRun.dParent;
      pitOptions = cloneOptions(sourceRun.pitOptions);
   }

   // **********************************************************************
   public void execute() throws MojoExecutionException
   {
      EntryPoint pitEntryPoint = null;
      AnalysisResult execResult = null;

      System.out.println("################ DescartesRun.execute");
      printInfo();

      pitEntryPoint = new EntryPoint();
      execResult = pitEntryPoint.execute(getBaseDir(), getPitOptions(),
         getPlugins(), getEnvironmentVariables());
      if (execResult.getError().hasSome())
      {
         throw new MojoExecutionException("fail", execResult.getError().value());
      }
      results = execResult.getStatistics().value();
   }

   // **********************************************************************
   public void mergeOptions(ReportOptions otherOptions)
   {
      // to do
   }

   // **********************************************************************
   public void printInfo()
   {
      System.out.println("#");
      System.out.println("# targetTests: " + getPitOptions().getTargetTests());
      System.out.println("# targetClasses: " + getPitOptions().getTargetClasses());
      System.out.println("# codePaths: " + getPitOptions().getCodePaths());
      System.out.println("# sourceDirs: " + getPitOptions().getSourceDirs());
      System.out.println("# classPathElements: " + getPitOptions().getClassPathElements());
      System.out.println("# mutationEngine: " + getPitOptions().getMutationEngine());
      System.out.println("#");

   }

   // **********************************************************************
   public ReportOptions cloneOptions(ReportOptions srcOptions)
   {
      ReportOptions newOptions = new ReportOptions();

      newOptions.setVerbose(srcOptions.isVerbose());
      newOptions.setReportDir(srcOptions.getReportDir());

      newOptions.setSourceDirs(new ArrayList<File>(srcOptions.getSourceDirs()));
      newOptions.setClassPathElements(new ArrayList<String>
         (srcOptions.getClassPathElements()));
      newOptions.setMutators(new ArrayList<String>(srcOptions.getMutators()));
      newOptions.setDependencyAnalysisMaxDistance
         (srcOptions.getDependencyAnalysisMaxDistance());
      newOptions.addChildJVMArgs(new ArrayList<String>
         (srcOptions.getJvmArgs()));
      newOptions.setTargetClasses(new ArrayList<Predicate<String>>
         (srcOptions.getTargetClasses()));
      
      newOptions.setMutateStaticInitializers
         (srcOptions.isMutateStaticInitializers());
      newOptions.setNumberOfThreads
         (srcOptions.getNumberOfThreads());
      newOptions.setTimeoutFactor
         (srcOptions.getTimeoutFactor());
      newOptions.setTimeoutConstant
         (srcOptions.getTimeoutConstant());

      newOptions.setLoggingClasses(new ArrayList<String>
         (srcOptions.getLoggingClasses()));
      newOptions.setExcludedMethods(new ArrayList<Predicate<String>>
         (srcOptions.getExcludedMethods()));
      newOptions.setMaxMutationsPerClass
         (srcOptions.getMaxMutationsPerClass());
      newOptions.setExcludedClasses(new ArrayList<Predicate<String>>
         (srcOptions.getExcludedClasses()));

      newOptions.addOutputFormats(new ArrayList<String>
         (srcOptions.getOutputFormats()));

      newOptions.setFailWhenNoMutations
         (srcOptions.shouldFailWhenNoMutations());
      newOptions.setCodePaths(new ArrayList<String>
         (srcOptions.getCodePaths()));
      newOptions.setMutationUnitSize(srcOptions.getMutationUnitSize());
      newOptions.setShouldCreateTimestampedReports
         (srcOptions.shouldCreateTimeStampedReports());
      newOptions.setDetectInlinedCode(srcOptions.isDetectInlinedCode());

      newOptions.setHistoryInputLocation(new File
         (srcOptions.getHistoryInputLocation().getPath()));
      newOptions.setHistoryOutputLocation(new File
         (srcOptions.getHistoryOutputLocation().getPath()));

      newOptions.setExportLineCoverage(srcOptions.shouldExportLineCoverage());
      newOptions.setMutationThreshold(srcOptions.getMutationThreshold());
      newOptions.setMutationEngine(srcOptions.getMutationEngine());
      newOptions.setCoverageThreshold(srcOptions.getCoverageThreshold());
      newOptions.setJavaExecutable(srcOptions.getJavaExecutable());
      newOptions.setIncludeLaunchClasspath(srcOptions.isIncludeLaunchClasspath());
      newOptions.setMaximumAllowedSurvivors(srcOptions.getMaximumAllowedSurvivors());
      newOptions.setExcludedRunners(new ArrayList<String>
         (srcOptions.getExcludedRunners()));

      newOptions.setGroupConfig(new TestGroupConfig
         (new ArrayList<String>(srcOptions.getGroupConfig().getExcludedGroups()),
          new ArrayList<String>(srcOptions.getGroupConfig().getIncludedGroups())));
      newOptions.setFreeFormProperties(new Properties(srcOptions.getFreeFormProperties()));

      return(newOptions);
   }

   // **********************************************************************
   // private
   // **********************************************************************
   // ******** attributes
   private DescartesProject dParent = null;
   private ReportOptions pitOptions = null;
   private CombinedStatistics results = null;
}
