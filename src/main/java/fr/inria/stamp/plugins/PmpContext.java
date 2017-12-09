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

   // **********************************************************************
   public PmpContext()
   {
      _Modules = null;
      _CurrentProject = null;
   }

   // **********************************************************************
   public void updateData(PmpMojo mojo)
   {
      List<MavenProject> moduleList = getProjectModules(mojo);

      // build once at the beginning; the complete list of project modules
      if (getModules() == null)
      {
         setModules(moduleList);
      }
   }

   // **********************************************************************
   public List<MavenProject> getProjectModules(PmpMojo mojo)
   {
      // <cael>: we take only the modules of the current tree, if parent project
      // <cael>  is located in another tree (i.e. mojo.getProject().getParent() != null),
      // <cael>  we need to browse the whole project graph to build project module list

      List<MavenProject> moduleList = mojo.getProject().getCollectedProjects();

      return(moduleList);
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

      return(module);
   }

   // **********************************************************************
   public static void printCollectedProjects(MavenProject aProject)
   {
      List<MavenProject> collectedProjects = aProject.getCollectedProjects();
      Iterator<MavenProject> myIt;
      MavenProject currentModule;

      System.out.print("#### collectedProjects(" + aProject.getArtifactId()
         + "): ");
      if (collectedProjects != null)
      {
         myIt = collectedProjects.iterator();
         while (myIt.hasNext())
         {  
            currentModule = myIt.next();
            System.out.print(currentModule.getArtifactId() + ", ");
         }
      }
      System.out.println("");
   }

   // **********************************************************************
   public static void printArtifacts(MavenProject aProject)
   {
      Set<Artifact> dependProjects = aProject.getArtifacts();
      Artifact currentModule;

      System.out.print("#### artifacts(" + aProject.getArtifactId()
         + "): ");
      if (dependProjects != null)
      {
         Iterator<Artifact> myIt = dependProjects.iterator();
         while (myIt.hasNext())
         {  
            currentModule = myIt.next();
            System.out.print(currentModule.getArtifactId() + ", ");
         }
      }
      System.out.println("");
   }

   // **********************************************************************
   public static void printDependModules(MavenProject aProject,
      ArrayList<MavenProject> moduleList)
   {
      MavenProject currentModule;

      System.out.print("#### dependModules(" + aProject.getArtifactId()
         + "): ");
      if (moduleList != null)
      {
         Iterator<MavenProject> myIt = moduleList.iterator();
         while (myIt.hasNext())
         {  
            currentModule = myIt.next();
            System.out.print(currentModule.getArtifactId() + ", ");
         }
      }
      System.out.println("");
   }

   // **********************************************************************
   // protected
   // **********************************************************************
   // ******** attributes
   protected static PmpContext _Instance = null;

   protected List<MavenProject> _Modules;
   protected PmpProject _CurrentProject;
}
