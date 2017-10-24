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
   public DescartesRun(DescartesRun sourceRun, ReportOptions otherPackageOptions)
   {
      dParent = sourceRun.dParent;
      pitOptions = mergeOptions(sourceRun.pitOptions, otherPackageOptions);
   }

   // **********************************************************************
   public void execute() throws MojoExecutionException
   {
      EntryPoint pitEntryPoint = null;
      AnalysisResult execResult = null;

      System.out.println("################################ DescartesRun.execute");
      printInfo(getPitOptions());

      pitEntryPoint = new EntryPoint();
      execResult = pitEntryPoint.execute(getBaseDir(), getPitOptions(),
         getPlugins(), getEnvironmentVariables());
      if (execResult.getError().hasSome())
      {
         throw new MojoExecutionException("fail", execResult.getError().value());
      }
      results = execResult.getStatistics().value();
      System.out.println("################################");
   }

   // **********************************************************************
   public void printInfo(ReportOptions data)
   {
      System.out.println("#");
      System.out.println("# targetTests: " + data.getTargetTests());
      System.out.println("# targetClasses: " + data.getTargetClasses());
      System.out.println("# codePaths: " + data.getCodePaths());
      System.out.println("# sourceDirs: " + data.getSourceDirs());
      System.out.println("# classPathElements: " + data.getClassPathElements());
      System.out.println("# mutationEngine: " + data.getMutationEngine());
      System.out.println("#");
   }

   // **********************************************************************
   public ReportOptions mergeOptions(ReportOptions srcOptions, ReportOptions classOptions)
   {
      ArrayList<Predicate<String>> targetClasses;
      ArrayList<Predicate<String>> codePaths;

      System.out.println("################################ DescartesRun.mergeOptions");
      printInfo(srcOptions);
      System.out.println("######## and");
      printInfo(classOptions);

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
      targetClasses = new ArrayList<Predicate<String>>(srcOptions.getTargetClasses());
      targetClasses.addAll(classOptions.getTargetClasses());
      newOptions.setTargetClasses(targetClasses);
      
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

      if (srcOptions.getHistoryInputLocation() != null)
      {
         newOptions.setHistoryInputLocation(new File
            (srcOptions.getHistoryInputLocation().getPath()));
      }
      if (srcOptions.getHistoryInputLocation() != null)
      {
         newOptions.setHistoryOutputLocation(new File
            (srcOptions.getHistoryOutputLocation().getPath()));
      }

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
