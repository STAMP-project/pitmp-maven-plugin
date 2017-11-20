package fr.inria.stamp.plugins;

// **********************************************************************
import java.util.List;
import java.util.ArrayList;
import java.io.File;

import org.apache.maven.project.MavenProject;
// import org.apache.maven.model.Model;
import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.MojoExecutionException;

import org.pitest.functional.Option;
import org.pitest.mutationtest.config.ReportOptions;
import org.pitest.mutationtest.config.PluginServices;
import org.pitest.mutationtest.tooling.CombinedStatistics;
import org.pitest.mutationtest.tooling.EntryPoint;
import org.pitest.mutationtest.tooling.AnalysisResult;
import org.pitest.maven.MojoToReportOptionsConverter;
import org.pitest.maven.SurefireConfigConverter;
import org.pitest.classpath.DirectoryClassPathRoot;

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
      return(getTheMojo().getProject().getArtifactId());
   }

   // **********************************************************************
   // ******** associations
   public PmpMojo getTheMojo()
   {
      return(_TheMojo);
   }

   // **********************************************************************
   public ReportOptions getRegularPitOptions()
   {
      return(_RegularPitOptions);
   }

   // **********************************************************************
   public void setRegularPitOptions(ReportOptions options)
   {
      _RegularPitOptions = options;
   }

   // **********************************************************************
   public ReportOptions getModifiedPitOptions()
   {
      return(_ModifiedPitOptions);
   }

   // **********************************************************************
   public void setModifiedPitOptions(ReportOptions options)
   {
      _ModifiedPitOptions = options;
   }

   // **********************************************************************
   public CombinedStatistics getResults()
   {
      return(_Results);
   }

   // **********************************************************************
   public int cardClassToMutateProjects()
   {
      return(_ClassToMutateProjects.size());
   }

   // **********************************************************************
   public PmpProject getClassToMutateProjects(int index)
   {
      PmpProject theProject = null;

      if (index >= 0 && index < cardClassToMutateProjects())
      {
         theProject = _ClassToMutateProjects.get(index);
      }

      return(theProject);
   }

   // **********************************************************************
   public void appendClassToMutateProjects(PmpProject aProject)
   {
      _ClassToMutateProjects.add(aProject);
   }

   // **********************************************************************
   // ******** methods
   public PmpProject(PmpMojo mojo)
   {
      _TheMojo = mojo;
      _ClassToMutateProjects = new ArrayList<PmpProject>();

      // System.out.println("# root Id: " + PmpContext.getInstance().getRootProject()
      //    .getArtifactId());
      // System.out.println("# project Id: " + _TheMojo.getProject().getArtifactId());
   }

   // **********************************************************************
   public CombinedStatistics execute() throws MojoExecutionException
   {
      EntryPoint pitEntryPoint = null;
      AnalysisResult execResult = null;

      System.out.println("################################ PmpProject.execute: IN");
      printInfo();

      // generateClassToMutateProjects();

      // create the ReportOptions as PiTest does and save it for futur use
      setRegularPitOptions(new MojoToReportOptionsConverter(getTheMojo(),
        new SurefireConfigConverter(), getTheMojo().getFilter()).convert());

      // now you can create the final ReportOptions
      setModifiedPitOptions(new MojoToReportOptionsConverter(getTheMojo(),
        new SurefireConfigConverter(), getTheMojo().getFilter()).convert());

      // and complete it to update codePaths:, sourceDirs and classPathElements
      modifyReportOptions();

      System.out.println("######## mojo");
      printMojoInfo();

      System.out.println("######## pitEntryPoint.execute");
      System.out.println("#");
      System.out.println("# (");
      System.out.println("# baseDir = " + getTheMojo().getBaseDir());
      System.out.println("#");
      System.out.println("# modified options = ");
      printOptionsInfo(getModifiedPitOptions());

      pitEntryPoint = new EntryPoint();
      execResult = pitEntryPoint.execute(getTheMojo().getBaseDir(),
         getModifiedPitOptions(), getTheMojo().getPlugins(),
         getTheMojo().getEnvironmentVariables());
      if (execResult.getError().hasSome())
      {
         throw new MojoExecutionException("fail", execResult.getError().value());
      }
      _Results = execResult.getStatistics().value();

      // return results
      System.out.println("################ PmpProject.execute: OUT");
      return(getResults());
   }

   // **********************************************************************
   public void generateClassToMutateProjects()
   {
      List<Dependency> myDependencies = getTheMojo().getProject().getDependencies();
      String projectName;
      PmpProject targetClassModule;
      ArrayList<String> completeTargetClasses = null;

      // System.out.println("################ PmpProject.generateClassToMutateProjects: IN");

      for (int i = 0; i < myDependencies.size(); i++)
      {
         projectName = myDependencies.get(i).getArtifactId();
         targetClassModule = PmpContext.getInstance().findInProjects(projectName);
         // System.out.println("# looking for: " + projectName);
         if (targetClassModule != null)
         {
            appendClassToMutateProjects(targetClassModule);
            // System.out.println("# found: " + targetClassModule.getName());
         }
      }
      // System.out.println("################ PmpProject.generateClassToMutateProjects: OUT");
   }

   // **********************************************************************
   public void modifyReportOptions()
   {
      ArrayList<File> fileList = null;
      ArrayList<String> stringList = null;

      // merge test and class source directories
      // <cael>: check if the order impacts the execution
      // <cael>: to do: mege avoiding duplication
      fileList = new ArrayList<File>(getRegularPitOptions().getSourceDirs());
      for (int i = 0; i < cardClassToMutateProjects(); i++)
      {
         fileList.addAll(getClassToMutateProjects(i).getRegularPitOptions()
            .getSourceDirs());
      }
      getModifiedPitOptions().setSourceDirs(fileList);

      // merge test and class class paths
      // <cael>: to do: checking conflicts
      // <cael>: to do: check merge order
      stringList = new ArrayList<String>(getRegularPitOptions().getClassPathElements());
      for (int i = 0; i < cardClassToMutateProjects(); i++)
      {
         PmpContext.addNewStrings(stringList,
            getClassToMutateProjects(i).getRegularPitOptions().getClassPathElements());
      }
      getModifiedPitOptions().setClassPathElements(stringList);

      // merge class code paths
      stringList = new ArrayList<String>(getRegularPitOptions().getCodePaths());
      for (int i = 0; i < cardClassToMutateProjects(); i++)
      {
         PmpContext.addNewStrings(stringList,
            getClassToMutateProjects(i).getRegularPitOptions().getCodePaths());
      }
      getModifiedPitOptions().setCodePaths(stringList);

      // <cael>: to do: merge ExcludedClasses
      // <cael>: to do: merge ExcludedMethods
   }

   // **********************************************************************
   public void printInfo()
   {
      List<Dependency> projectDependencies = getTheMojo().getProject().getDependencies();

      System.out.println("######## project: " + getName());
      System.out.println("#");
      if (getTheMojo().getProject().getParent() != null)
      {
         System.out.println("# Mvn Parent: " + getTheMojo().getProject().getParent()
            .getArtifactId());
      }
      System.out.print("# Dependencies: ");
      for (int i = 0; i < cardClassToMutateProjects(); i++)
      {
         System.out.print(getClassToMutateProjects(i).getName() + ", ");
      }
      System.out.println("");
      System.out.println("########");
   }

   // **********************************************************************
   public void printMojoInfo()
   {
      System.out.println("######## mojo attributes");
      System.out.println("#");
      System.out.println("# targetTests: " + getTheMojo().getTargetTests());
      System.out.println("# mutationEngine: " + getTheMojo().getMutationEngine());
      System.out.println("# targetClasses: " + getTheMojo().getTargetClasses());
      System.out.println("# modified targetClasses: " + getTheMojo()
         .getTargetClasses());
      // System.out.println("# excludedClasses: " + getTheMojo()
         // .getModifiedExcludedClasses());
      // System.out.println("# excludedMethods: " + getTheMojo()
         // .getModifiedExcludedMethods());
      System.out.println("#");
      System.out.println("########");
   }

   // **********************************************************************
   public void printOptionsInfo(ReportOptions data)
   {
      System.out.println("#");
      System.out.println("# mutationEngine: " + data.getMutationEngine());
      System.out.println("# targetTests: " + data.getTargetTests());
      System.out.println("# targetClasses: " + data.getTargetClasses());
      System.out.println("# excludedClasses: " + data.getExcludedClasses());
      System.out.println("# excludedMethods: " + data.getExcludedMethods());
      System.out.println("# codePaths: " + data.getCodePaths());
      System.out.println("# sourceDirs: " + data.getSourceDirs());
      System.out.println("# classPathElements: " + data.getClassPathElements());
      System.out.println("#");
   }

   // **********************************************************************

   // **********************************************************************
   // protected
   // **********************************************************************
   // ******** associations
   protected PmpMojo _TheMojo = null;
   protected ReportOptions _RegularPitOptions = null;
   protected ReportOptions _ModifiedPitOptions = null;
   protected CombinedStatistics _Results = null;
   protected ArrayList<PmpProject> _ClassToMutateProjects = null;
}
