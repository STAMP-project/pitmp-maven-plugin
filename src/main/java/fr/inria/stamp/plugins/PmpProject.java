package fr.inria.stamp.plugins;

// **********************************************************************
import java.util.List;
import java.util.Set;
import java.util.ArrayList;
import java.util.Iterator;
import java.io.File;

import org.apache.maven.artifact.Artifact;
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
   public MavenProject getTheMavenProject()
   {
      return(getTheMojo().getProject());
   }

   // **********************************************************************
   public ReportOptions getPitOptions()
   {
      return(_PitOptions);
   }

   // **********
   public void setPitOptions(ReportOptions options)
   {
      _PitOptions = options;
   }

   // **********************************************************************
   public CombinedStatistics getResults()
   {
      return(_Results);
   }

   // **********************************************************************
   public ArrayList<String> getSourceDirs()
   // merge the source and test source dirs for all dependencies
   {
      ArrayList<String> completeList = new ArrayList<String>();
      Set<Artifact> dependList = getTheMavenProject().getArtifacts();
      Iterator<Artifact> myIt = dependList.iterator();
      Artifact currentDepend;
      MavenProject theProject;

      addSourceDirs(completeList, getTheMavenProject());

      // and add classes of every dependencies which are project modules
      while (myIt.hasNext())
      {
         currentDepend = myIt.next();
         theProject = PmpContext.getInstance().getMavenProjectFromName
            (currentDepend.getArtifactId());
         if (theProject != null)
         {
            addSourceDirs(completeList, theProject);
         }
      }

      return(completeList);
   }

   // **********************************************************************
   public ArrayList<String> getDependsCodePaths()
   {
      ArrayList<String> codePaths = new ArrayList<String>();
      Set<Artifact> dependList = getTheMavenProject().getArtifacts();
      Iterator<Artifact> myIt = dependList.iterator();
      Artifact currentDepend;
      MavenProject theProject;

      // and add paths of every dependencies which are project modules
      while (myIt.hasNext())
      {
         currentDepend = myIt.next();
         theProject = PmpContext.getInstance().getMavenProjectFromName
            (currentDepend.getArtifactId());
         if (theProject != null)
         {
            codePaths.add(theProject.getBuild().getOutputDirectory());
         }
      }

      return(codePaths);
   }

   // **********************************************************************
   public ArrayList<String> getDependsClassPathElements()
   {
      ArrayList<String> completeList = new ArrayList<String>();
      Set<Artifact> dependList = getTheMavenProject().getArtifacts();
      MavenProject dependProject;
      Iterator<Artifact> myIt = dependList.iterator();
      Artifact currentDepend;
      String pathName;

      // System.out.println("#### cpElts(" + getName() + "): ");
      // and add paths of every dependencies which are project modules
      while (myIt.hasNext())
      {
         currentDepend = myIt.next();
         // System.out.println("####     currentDepend: " + currentDepend.getArtifactId());

         dependProject = PmpContext.getInstance().getMavenProjectFromName
            (currentDepend.getArtifactId());
         // System.out.println("####     dependProject: " + dependProject);
         if (dependProject != null)
         {
            completeList.add(dependProject.getBuild().getOutputDirectory());
            completeList.add(dependProject.getBuild().getTestOutputDirectory());
            // System.out.println("####     " + currentDepend.getArtifactId() + " - od: "
              // + dependProject.getBuild().getOutputDirectory());
            // System.out.println("####     " + currentDepend.getArtifactId() + " - tod: "
              // + dependProject.getBuild().getTestOutputDirectory());
         }

         if (! currentDepend.getType().equals("pom"))
         {
            pathName = currentDepend.getFile().getAbsolutePath();
            // System.out.println("####     " + currentDepend.getArtifactId()
               // + " - type: " + currentDepend.getType()
               // + " - path: " + pathName);
            completeList.add(pathName);
         }
      }

      return(completeList);
   }

   // **********************************************************************
   // ******** methods
   public PmpProject(PmpMojo mojo)
   {
      _TheMojo = mojo;

      // System.out.println("# project Id: " + getName());
   }

   // **********************************************************************
   public CombinedStatistics execute() throws MojoExecutionException
   {
      EntryPoint pitEntryPoint = null;
      AnalysisResult execResult = null;

      // System.out.println("################################ PmpProject.execute: IN");
      // printInfo();

      // printMojoInfo();

      // create the final ReportOptions
      setPitOptions(new MojoToReportOptionsConverter(getTheMojo(),
           new SurefireConfigConverter(), getTheMojo().getFilter())
         .convert());

      // System.out.println("######## pit options:");
      // printOptionsInfo(getPitOptions());

      // and complete it to update codePaths, sourceDirs and classPathElements
      modifyReportOptions();

      // System.out.println("######## modified options:");
      // printOptionsInfo(getPitOptions());

      // System.out.println("######## pitEntryPoint.execute(baseDir = " +
         // getTheMojo().getBaseDir() + ")");
      pitEntryPoint = new EntryPoint();
      execResult = pitEntryPoint.execute(getTheMojo().getBaseDir(),
         getPitOptions(), getTheMojo().getPlugins(),
         getTheMojo().getEnvironmentVariables());
      if (execResult.getError().hasSome())
      {
         throw new MojoExecutionException("fail", execResult.getError().value());
      }
      _Results = execResult.getStatistics().value();

      // return results
      // System.out.println("################ PmpProject.execute: OUT");
      return(getResults());
   }

   // **********************************************************************
   public void modifyReportOptions()
   {
      // require(getPitOptions() != null)
      ArrayList<File> fileList = null;
      ArrayList<String> sourceDirList = null;
      ArrayList<String> codePaths = null;
      ArrayList<String> dependsCodePaths = null;
      ArrayList<String> classPathElts = null;
      ArrayList<String> dependsClassPathElts = null;

      // merge test and class source directories
      // <cael>: to do: check if the order impacts the execution
      sourceDirList = getSourceDirs();
      if (sourceDirList != null && ! sourceDirList.isEmpty())
      {
         fileList = PmpContext.stringsToFiles(sourceDirList);
         getPitOptions().setSourceDirs(fileList);
      }

      dependsCodePaths = getDependsCodePaths();
      if (getPitOptions().getCodePaths() != null &&
          ! getPitOptions().getCodePaths().isEmpty())
      {
         codePaths = new ArrayList<String>(getPitOptions().getCodePaths());
         if (dependsCodePaths != null && ! dependsCodePaths.isEmpty())
         {
            PmpContext.addNewStrings(codePaths, dependsCodePaths);
         }
      }
      else
      {
         codePaths = dependsCodePaths;
      }
      if (codePaths != null && ! codePaths.isEmpty())
      {
         getPitOptions().setCodePaths(codePaths);
      }

      dependsClassPathElts = getDependsClassPathElements();
      if (getPitOptions().getClassPathElements() != null &&
          ! getPitOptions().getClassPathElements().isEmpty())
      {
         classPathElts = new ArrayList<String>(getPitOptions().getClassPathElements());
         if (dependsClassPathElts != null && ! dependsClassPathElts.isEmpty())
         {
            PmpContext.addNewStrings(classPathElts, dependsClassPathElts);
         }
      }
      else
      {
         classPathElts = dependsClassPathElts;
      }
      if (classPathElts != null && ! classPathElts.isEmpty())
      {
         getPitOptions().setClassPathElements(classPathElts);
      }
   }

   // **********************************************************************
   public Boolean hasCompileSourceRoots()
   // the module or one of the dependencies has target/classes
   {
      Boolean result = PmpContext.oneFileExists
         (getTheMojo().getProject().getCompileSourceRoots());
      ArrayList<MavenProject> dependList = PmpContext.getInstance()
         .getDependingModules(getTheMojo().getProject());
      Iterator<MavenProject> myIt = dependList.iterator();
      MavenProject currentModule;

      // System.out.println("#### hasCompileSourceRoots(" + getName() + "): " +
         // result + " - compSrcRoots: " + getTheMojo().getProject().getCompileSourceRoots());
      // check for dependencies
      while (myIt.hasNext() && ! result)
      {
         currentModule = myIt.next();
         result = PmpContext.oneFileExists(currentModule.getCompileSourceRoots());
         // System.out.println("####     dep " + currentModule.getArtifactId() + ": " +
            // result + " - compSrcRoots: " + currentModule.getCompileSourceRoots());
      }

      return(result);
   }

   // **********************************************************************
   public void addSourceDirs(List<String> listToComplete,
      MavenProject theProject)
   {
      // require(getPitOptions() != null)
      List<String> nameList;

      nameList = theProject.getCompileSourceRoots();
      if (nameList != null && ! nameList.isEmpty())
      {
         listToComplete.addAll(nameList);
      }
      nameList = theProject.getTestCompileSourceRoots();
      if (nameList != null && ! nameList.isEmpty())
      {
         listToComplete.addAll(nameList);
      }
   }

   // **********************************************************************
   public void printInfo()
   {
      System.out.println("#### project: " + getName());
      System.out.println("#");
      if (getTheMojo().getProject().getParent() != null)
      {
         System.out.println("# Mvn Parent: " + getTheMojo().getProject().getParent()
            .getArtifactId());
      }
      System.out.println("# Mvn all dependencies: " +
         getTheMojo().getProject().getArtifacts());
      System.out.println("");
      System.out.println("####");
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
      System.out.println("#");
      System.out.println("########");
   }

   // **********************************************************************
   public void printOptionsInfo(ReportOptions data)
   {
      System.out.println("####");
      System.out.println("#### mutationEngine: " + data.getMutationEngine());
      System.out.println("#### targetTests: " + data.getTargetTests());
      System.out.println("#### excludedClasses: " + data.getExcludedClasses());
      System.out.println("#### excludedMethods: " + data.getExcludedMethods());
      System.out.println("#### targetClasses: " + data.getTargetClasses());
      System.out.println("#### codePaths: " + data.getCodePaths());
      System.out.println("#### sourceDirs: " + data.getSourceDirs());
      System.out.println("#### classPathElements: " + data.getClassPathElements());
      System.out.println("####");
   }

   // **********************************************************************

   // **********************************************************************
   // protected
   // **********************************************************************
   // ******** associations
   protected PmpMojo _TheMojo = null;
   protected ReportOptions _PitOptions = null;
   protected CombinedStatistics _Results = null;
}
