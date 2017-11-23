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
   public List<String> getTargetClasses()
   {
      // System.out.println("######## PmpProject.getTargetClasses: IN");
      ArrayList<String> completeList = new ArrayList<String>
         (getTheMojo().getLocalTargetClasses());

      // add other modules excluded methods
      // System.out.print("# dependencies: ");
      for (int i = 0; i < cardClassToMutateProjects(); i++)
      {
         // System.out.print(getClassToMutateProjects(i).getName() + ", ");
         PmpContext.addNewStrings(completeList,
            getClassToMutateProjects(i).getTargetClasses());
      }
      // System.out.println("");

      // System.out.println("######## PmpProject.getTargetClasses: OUT");
      return(completeList);
   }

   // **********************************************************************
   public List<String> getExcludedClasses()
   {
      // System.out.println("######## PmpProject.getExcludedClasses: IN");
      ArrayList<String> completeList = new ArrayList<String>
         (getTheMojo().getLocalExcludedClasses());

      // add other modules excluded methods
      // System.out.print("# dependencies: ");
      for (int i = 0; i < cardClassToMutateProjects(); i++)
      {
         // System.out.print(getClassToMutateProjects(i).getName() + ", ");
         PmpContext.addNewStrings(completeList,
            getClassToMutateProjects(i).getExcludedClasses());
      }
      // System.out.println("");

      // System.out.println("######## PmpProject.getExcludedClasses: OUT");
      return(completeList);
   }

   // **********************************************************************
   public List<String> getExcludedMethods()
   {
      // System.out.println("######## PmpProject.getExcludedMethods: IN");
      ArrayList<String> completeList = new ArrayList<String>
         (getTheMojo().getLocalExcludedMethods());

      // add other modules excluded methods
      // System.out.print("# dependencies: ");
      for (int i = 0; i < cardClassToMutateProjects(); i++)
      {
         // System.out.print(getClassToMutateProjects(i).getName() + ", ");
         PmpContext.addNewStrings(completeList,
            getClassToMutateProjects(i).getExcludedMethods());
      }
      // System.out.println("");

      // System.out.println("######## PmpProject.getExcludedMethods: OUT");
      return(completeList);
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

      // create the final ReportOptions
      setModifiedPitOptions(new MojoToReportOptionsConverter(getTheMojo(),
           new SurefireConfigConverter(), getTheMojo().getFilter())
         .convert());

      // and complete it to update codePaths, sourceDirs and classPathElements
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

      // System.out.println("# project: " + getName());
      for (int i = 0; i < myDependencies.size(); i++)
      {
         projectName = myDependencies.get(i).getArtifactId();
         targetClassModule = PmpContext.getInstance().findInProjects(projectName);
         // System.out.print("# looking for: " + projectName + ": ");
         if (targetClassModule != null)
         {
            appendClassToMutateProjects(targetClassModule);
            // System.out.println("found");
         }
         // else
         // {
         //    System.out.println("ignored");
         // }
      }
      // System.out.println("################ PmpProject.generateClassToMutateProjects: OUT");
   }

   // **********************************************************************
   public void modifyReportOptions()
   {
      ArrayList<File> fileList = null;
      ArrayList<String> classPathList = null;
      ArrayList<String> codePathList = null;
      ReportOptions theOptions;

      // merge test and class source directories
      // <cael>: to do: check if the order impacts the execution
      fileList = new ArrayList<File>(getRegularPitOptions().getSourceDirs());
      classPathList = new ArrayList<String>
         (getRegularPitOptions().getClassPathElements());
      codePathList = new ArrayList<String>(getRegularPitOptions().getCodePaths());
      for (int i = 0; i < cardClassToMutateProjects(); i++)
      {
         theOptions = getClassToMutateProjects(i).getRegularPitOptions();
         fileList.addAll(theOptions.getSourceDirs());
         // merge test and class class paths
         // <cael>: to do: checking conflicts
         // <cael>: to do: check merge order
         PmpContext.addNewStrings(classPathList, theOptions.getClassPathElements());
         PmpContext.addNewStrings(codePathList, theOptions.getCodePaths());
      }
      getModifiedPitOptions().setSourceDirs(fileList);
      getModifiedPitOptions().setClassPathElements(classPathList);
      getModifiedPitOptions().setCodePaths(codePathList);
   }

   // **********************************************************************
   public Boolean hasCompileSourceRoots()
   // the module or one of the submodules has target classes
   {
      Boolean result = PmpContext.oneFileExists
         (getTheMojo().getProject().getCompileSourceRoots());

      for (int i = 0; i < cardClassToMutateProjects() && ! result; i++)
      {
         result = getClassToMutateProjects(i).hasCompileSourceRoots();
      }

      return(result);
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
