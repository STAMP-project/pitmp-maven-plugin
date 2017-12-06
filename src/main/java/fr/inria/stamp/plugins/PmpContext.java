package fr.inria.stamp.plugins;

// **********************************************************************
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.ArrayList;
import java.util.Iterator;
import java.io.File;

import org.apache.maven.project.MavenProject;
import org.apache.maven.artifact.Artifact;

import org.pitest.maven.AbstractPitMojo;
import org.pitest.classpath.DirectoryClassPathRoot;

// **********************************************************************
public class PmpContext
{
   // **********************************************************************
   // public
   // **********************************************************************
   // ******** associations
   public static PmpContext getInstance()
   {
      if (_Instance == null)
      {
         _Instance = new PmpContext();
      }
      return(_Instance);
   }

   // **********************************************************************
   public List<MavenProject> getModules()
   {
      return(_Modules);
   }

   // **********
   protected void setModules(List<MavenProject> value)
   {
      _Modules = value;
   }

   // **********************************************************************
   public PmpProject getCurrentProject()
   {
      return(_CurrentProject);
   }

   // **********
   public void setCurrentProject(PmpProject value)
   {
      _CurrentProject = value;
   }

   // **********************************************************************
   // ******** methods
   public static void addNewStrings(List<String> targetList,
      List<String> listToAdd)
   {
      Iterator<String> myIt = listToAdd.iterator();
      String currentElt;

      while (myIt.hasNext())
      {  
         currentElt = myIt.next();
         if (! targetList.contains(currentElt))
         {
            targetList.add(currentElt);
         }
      }
   }

   // **********************************************************************
   public static ArrayList<File> stringsToFiles(ArrayList<String> nameList)
   {
      ArrayList<File> fileList = new ArrayList<File>();
      File newFile;

      for (int i = 0; i < nameList.size(); i++)
      {
         newFile = new File(nameList.get(i));
         if (newFile.exists())
         {
            fileList.add(newFile);
         }
      }

      return(fileList);
   }

   // **********************************************************************
   public static Boolean oneFileExists(List<String> fileNameList)
   {
      Iterator<String> myIt = fileNameList.iterator();
      String currentName;
      Boolean result = false;
      File theFile;

      while (myIt.hasNext() && ! result)
      {  
         currentName = myIt.next();
         theFile = new File(currentName);
         result = theFile.exists();
      }

      return(result);
   }

   // **********************************************************************
   public static ArrayList<String> getClasses(MavenProject theProject)
   {
      ArrayList<String> classList = new ArrayList<String>();
      String outputDirName = theProject.getBuild().getOutputDirectory();
      File outputDir = new File(outputDirName);

      if (outputDir.exists())
      {
         DirectoryClassPathRoot classRoot = new DirectoryClassPathRoot(outputDir);
         classList.addAll(classRoot.classNames());
      }
      // else
      // <cael>: check if this could happen, and what does it mean

      return(classList);
   }

/*
   // **********************************************************************
   public static ArrayList<String> getClassPathElts(MavenProject theProject)
   {
      Logger pitLogger = Log.getLogger();
      ArrayList<String> pathList = new ArrayList<String>();
      List<String> tmpList = null;

      // should fit MojoToReportOptionsConverter.convert
      // would be nice to have a separate method to compute ClassPath
      try
      {
         tmpList = theProject.getTestClasspathElements();
         pathList.addAll(tmpList);
      }
      catch (final DependencyResolutionRequiredException e1)
      {
         pitLogger.info(e1);
      }

      addOwnDependenciesToClassPath(pathList);

      // <cael>: to do: how can we theProject.mojo.getAdditionalClasspathElements() ?
      // and theProject.mojo.getClasspathDependencyExcludes() ?

      for (Object artifact : getCurrentProject().getTheMojo().getProject().getArtifacts())
      {
         Artifact dependency = (Artifact)artifact;

         if (getCurrentProject().getTheMojo().getClasspathDependencyExcludes().contains
                (dependency.getGroupId() + ":" + dependency.getArtifactId()))
         {
            pathList.remove(dependency.getFile().getPath());
         }
      }

      return(pathList);
   }

   // **********************************************************************
   public void addOwnDependenciesToClassPath(ArrayList<String> classPath)
   {
      for (final Artifact dependency : filteredDependencies())
      {
        this.log.info("Adding " + dependency.getGroupId() + ":"
            + dependency.getArtifactId() + " to SUT classpath");
        classPath.add(dependency.getFile().getAbsolutePath());
      }
   }

   // **********************************************************************
   private Collection<Artifact> filteredDependencies()
   {
      return(FCollection.filter(getTheCurrentProject().getTheMojo()
         .getPluginArtifactMap().values(), this.dependencyFilter));
   }
*/
   // **********************************************************************
   public PmpContext()
   {
      _Modules = null;
      _CurrentProject = null;
   }

   // **********************************************************************
   public void updateData(PmpMojo mojo)
   {
      MavenProject rootProject = getRootProject(mojo);
      List<MavenProject> moduleList = rootProject.getCollectedProjects();

      // build once at the beginning; the complete list of project modules
      if (getModules() == null)
      {
         setModules(moduleList);
      }

      // System.out.println("#### " + rootProject.getArtifactId() +
         // " modules: " + rootProject.getModules());
      // printCollectedProjects(rootProject);
      // printArtifacts(mojo.getProject());

      // System.out.println("#### modules: " + getModules());
   }

   // **********************************************************************
   public MavenProject getRootProject(PmpMojo mojo)
   {
      // <cael>: to do: decide where to stop, project physical tree or following
      // all "parents" ?
      // code this method according to the choice
      MavenProject rootProject = mojo.getProject();

      return(rootProject);
   }

   // **********************************************************************
   public ArrayList<MavenProject> getDependingModules(MavenProject aProject)
   {
      ArrayList<MavenProject> moduleList = new ArrayList<MavenProject>();
      Set<Artifact> allDepend = aProject.getArtifacts();
      Iterator<Artifact> myIt = allDepend.iterator();
      Artifact currentArtifact;
      MavenProject aModule;

      while (myIt.hasNext())
      {
         currentArtifact = myIt.next();
         aModule = getMavenProjectFromName(currentArtifact.getArtifactId());
         if (aModule != null)
         {
            moduleList.add(aModule);
         }
      }

      return(moduleList);
   }

   // **********************************************************************
   public Boolean isProjectModule(String name)
   {
      Iterator<MavenProject> myIt = getModules().iterator();
      MavenProject currentModule;
      Boolean result = false;

      while (myIt.hasNext() && ! result)
      {  
         currentModule = myIt.next();
         result = currentModule.getArtifactId().equals(name);
      }
      // System.out.println("#### isProjectModule: (" + name + ") = " + result);

      return(result);
   }

   // **********************************************************************
   public MavenProject getMavenProjectFromName(String name)
   {
      Iterator<MavenProject> myIt = getModules().iterator();
      MavenProject currentModule;
      MavenProject module = null;

      while (myIt.hasNext() && module == null)
      {  
         currentModule = myIt.next();
         if (currentModule.getArtifactId().equals(name))
         {
            module = currentModule;
         }
      }
      // System.out.println("#### getMavenProjectFromName(" + name + ") = " + module);

      return(module);
   }

   // **********************************************************************
   public static void printCollectedProjects(MavenProject aProject)
   {
      List<MavenProject> collectedProjects = aProject.getCollectedProjects();
      Iterator<MavenProject> myIt;
      MavenProject currentModule;

      // System.out.print("#### collectedProjects(" + aProject.getArtifactId()
         // + "): ");
      if (collectedProjects != null)
      {
         myIt = collectedProjects.iterator();
         while (myIt.hasNext())
         {  
            currentModule = myIt.next();
            // System.out.print(currentModule.getArtifactId() + ", ");
         }
      }
      // System.out.println("");
   }

   // **********************************************************************
   public static void printArtifacts(MavenProject aProject)
   {
      Set<Artifact> dependProjects = aProject.getArtifacts();
      Artifact currentModule;

      // System.out.print("#### artifacts(" + aProject.getArtifactId()
         // + "): ");
      if (dependProjects != null)
      {
         Iterator<Artifact> myIt = dependProjects.iterator();
         while (myIt.hasNext())
         {  
            currentModule = myIt.next();
            // System.out.print(currentModule.getArtifactId() + ", ");
         }
      }
      // System.out.println("");
   }

   // **********************************************************************
   public static void printDependModules(MavenProject aProject,
      ArrayList<MavenProject> moduleList)
   {
      MavenProject currentModule;

      // System.out.print("#### dependModules(" + aProject.getArtifactId()
         // + "): ");
      if (moduleList != null)
      {
         Iterator<MavenProject> myIt = moduleList.iterator();
         while (myIt.hasNext())
         {  
            currentModule = myIt.next();
            // System.out.print(currentModule.getArtifactId() + ", ");
         }
      }
      // System.out.println("");
   }

   // **********************************************************************
   // protected
   // **********************************************************************
   // ******** attributes
   protected static PmpContext _Instance = null;

   protected List<MavenProject> _Modules;
   protected PmpProject _CurrentProject;
}
