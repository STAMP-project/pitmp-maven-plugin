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
import org.pitest.classpath.DirectoryClassPathRoot;

import org.pitest.maven.AbstractPitMojo;
import org.pitest.maven.GoalStrategy;
import org.pitest.maven.DependencyFilter;
import org.pitest.maven.NonEmptyProjectCheck;
import org.pitest.maven.MojoToReportOptionsConverter;
import org.pitest.maven.SurefireConfigConverter;

// **********************************************************************
public class PmpRun
{
   // **********************************************************************
   // public
   // **********************************************************************
   // ******** associations
   public PmpProject getParent()
   {
      return(parentProject);
   }

   // **********************************************************************
   public PmpProject getClassToMutateProject()
   {
      return(classToMutateProject);
   }

   // **********************************************************************
   public ReportOptions getPitOptions()
   {
      return(_PitOptions);
   }

   // **********************************************************************
   public void setPitOptions(ReportOptions options)
   {
      _PitOptions = options;
   }

   // **********************************************************************
   public CombinedStatistics getResults()
   {
      return(results);
   }

   // **********************************************************************
   // ******** methods
   // constructor for the regular pitest run
   public PmpRun(PmpProject theParent)
   {
      parentProject = theParent;
      classToMutateProject = theParent;
   }

   // **********************************************************************
   // constructor for additionnal runs
   public PmpRun(PmpRun testSuiteRun, PmpProject targetClassProject)
   {
      parentProject = testSuiteRun.parentProject;
      classToMutateProject = targetClassProject;
   }

   // **********************************************************************
   public void execute() throws MojoExecutionException
   {
      EntryPoint pitEntryPoint = null;
      AnalysisResult execResult = null;
      PmpMojo testMojo = getParent().getTheMojo();
      PmpMojo classMojo = getClassToMutateProject().getTheMojo();

      System.out.println("################################ PmpRun.execute: IN");
      System.out.println("# testProject: " + getParent().getName());
      System.out.println("# classProject: " + getClassToMutateProject().getName());
      System.out.println("# classProject.outputDir: " +
         classMojo.getProject().getBuild().getOutputDirectory());

      pitEntryPoint = new EntryPoint();

      // update the test mojo with the target classes and relative data
      // except for the regular run
      if (getParent() != getClassToMutateProject())
      {
         modifyTestMojo();
      }

      System.out.println("######## mojo attributes");
      System.out.println("#");
      System.out.println("# Mojo targetTests: " + testMojo.getTargetTests());
      System.out.println("# Mojo targetClasses: " + testMojo.getModifiedTargetClasses());
      // System.out.println("# Mojo excludedClasses: " + testMojo.getModifiedExcludedClasses());
      // System.out.println("# Mojo excludedMethods: " + testMojo.getModifiedExcludedMethods());
      System.out.println("#");

      // now you can create the ReportOptions and call Pit
      setPitOptions(new MojoToReportOptionsConverter(testMojo,
        new SurefireConfigConverter(), testMojo.getFilter()).convert());

      // and modify it to update codePaths:, sourceDirs and classPathElements
      if (getParent() != getClassToMutateProject())
      {
         modifyReportOptions();
      }

      System.out.println("######## pitEntryPoint.execute");
      System.out.println("#");
      System.out.println("# (");
      System.out.println("# baseDir = " + testMojo.getBaseDir());
      System.out.println("# options = ");
      printInfo(getPitOptions());
      System.out.println("# )");

      execResult = pitEntryPoint.execute(testMojo.getBaseDir(), getPitOptions(),
         testMojo.getPlugins(), testMojo.getEnvironmentVariables());
      if (execResult.getError().hasSome())
      {
         throw new MojoExecutionException("fail", execResult.getError().value());
      }
      results = execResult.getStatistics().value();

      // ran at least once, so not the regular mojo anymore
      testMojo.setIsRegularMojo(false);

      System.out.println("################ results");
      System.out.println(results);
      System.out.println("################################ PmpRun.execute: OUT");
   }

   // **********************************************************************
   public void modifyTestMojo()
   {
      PmpMojo testMojo = getParent().getTheMojo();
      PmpMojo classMojo = getClassToMutateProject().getTheMojo();
      ArrayList<String> classes = null;

      if (classMojo.getTargetClasses() == null || classMojo.getTargetClasses().isEmpty())
      // if empty, Pit will mutate all classes of testMojo, like for the regular run
      // so we need to get the classes
      {
         String outputDirName = classMojo.getProject().getBuild()
            .getOutputDirectory();
         File outputDir = new File(outputDirName);
         if (outputDir.exists())
         {
            DirectoryClassPathRoot classRoot = new DirectoryClassPathRoot(outputDir);
            classes = new ArrayList<String>(classRoot.classNames());
         }
      }
      else
      // just copy the target class list specified in the pom
      {
         classes = new ArrayList<String>(classMojo.getTargetClasses());
      }
      testMojo.setModifiedTargetClasses(classes);
   }

   // **********************************************************************
   public void modifyReportOptions()
   {
      ReportOptions classOptions = getClassToMutateProject().getTestRuns(0)
         .getPitOptions();
      ArrayList<File> fileList = null;
      ArrayList<String> stringList = null;

      // merge test and class source directories
      // CaeL: check if the order impacts the execution
      fileList = new ArrayList<File>(getPitOptions().getSourceDirs());
      fileList.addAll(classOptions.getSourceDirs());
      getPitOptions().setSourceDirs(fileList);

      // merge test and class class paths
      // CaeL: to do: merge checking conflicts and avoiding duplication
      // CaeL: to do: merge order ?
      stringList = new ArrayList<String>(getPitOptions().getClassPathElements());
      stringList.addAll(classOptions.getClassPathElements());
      getPitOptions().setClassPathElements(stringList);

      getPitOptions().setCodePaths(new ArrayList<String>(classOptions.getCodePaths()));
   }

   // **********************************************************************
   public void printInfo(ReportOptions data)
   {
      System.out.println("#");
      System.out.println("# targetTests: " + data.getTargetTests());
      System.out.println("# targetClasses: " + data.getTargetClasses());
      // System.out.println("# excludedClasses: " + data.getExcludedClasses());
      // System.out.println("# excludedMethods: " + data.getExcludedMethods());
      System.out.println("# codePaths: " + data.getCodePaths());
      System.out.println("# sourceDirs: " + data.getSourceDirs());
      System.out.println("# classPathElements: " + data.getClassPathElements());
      System.out.println("# mutationEngine: " + data.getMutationEngine());
      System.out.println("#");
   }

   // **********************************************************************
   // private
   // **********************************************************************
   // ******** attributes
   private PmpProject parentProject = null;
   private PmpProject classToMutateProject = null;
   private CombinedStatistics results = null;
   private ReportOptions _PitOptions = null;
}
