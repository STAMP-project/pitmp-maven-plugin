package fr.inria.stamp.plugins;

// **********************************************************************
import java.util.Collection;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.maven.project.MavenProject;

import org.pitest.maven.AbstractPitMojo;

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
   public MavenProject getRootProject()
   {
      return(rootProject);
   }

   // **********************************************************************
   public PmpProject getCurrentProject()
   {
      PmpProject theProject = null;

      if (currentProjectIndex >= 0 && currentProjectIndex < cardProjects())
      {
         theProject = getProjects(currentProjectIndex);
      }

      return(theProject);
   }

   // **********************************************************************
   public int cardProjects()
   {
      return(_Projects.size());
   }

   // ***********
   public PmpProject getProjects(int index)
   {
      PmpProject theProject = null;

      if (index >= 0 && index < cardProjects())
      {
         theProject = _Projects.get(index);
      }

      return(theProject);
   }

   // ***********
   public void appendProjects(PmpProject aProject)
   {
      // System.out.println("######## appendProjects: " + aProject);
      // System.out.println("# name = " + aProject.getName() + " - card = " +
      //   cardProjects());
      // printInfo(true);
      System.out.println("#");

      _Projects.add(aProject);
      currentProjectIndex = cardProjects() - 1;

      // System.out.println("# addedProject: " + getProjects(cardProjects() - 1));
      // System.out.println("# currentProject: " + getCurrentProject());
      // System.out.println("# name = " + getCurrentProject().getName() + " - card = " +
      //    cardProjects());
      // printInfo(true);
   }

   // ***********
   public PmpProject findInProjects(String aName)
   {
      PmpProject theProject = null;
      String id;

      // System.out.println("# findInProjects: " + aName + " - card = " + cardProjects());
      for (int i = 0; (i < cardProjects() && theProject == null); i++)
      {
         id = getProjects(i).getTheMojo().getProject().getArtifactId();
         // System.out.println("# i: " + i + " - Id = " + id);
         if (id.equals(aName))
         {
            theProject = getProjects(i);
         }
      }

      return(theProject);
   }

   // **********************************************************************
   // ******** methods
   public static void addNewStrings(ArrayList<String> targetList,
      Collection<String> listToAdd)
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
   public PmpContext()
   {
      _Projects = new ArrayList<PmpProject>();
      int currentProjectIndex = -1;
   }

   // **********************************************************************
   public void updateData(PmpMojo mojo)
   {
      if (rootProject == null)
      {
         if (mojo.getProject().getParent() != null)
         // no parents means non multi-module project
         {
            rootProject = mojo.getProject().getParent();
         }
      }
   }

   // **********************************************************************
   public void printInfo(boolean printAll)
   {
      System.out.println("######## Context: cardProjects = " + cardProjects());
      System.out.println("# currentProjectIndex = " + currentProjectIndex);

      if (getCurrentProject() != null)
      {
         System.out.println("# current project: " +
            getCurrentProject().getTheMojo().getProject().getArtifactId());
      }

      if (printAll)
      {
         for (int i = 0; i < cardProjects(); i++)
         {
            System.out.println("# project[" + i + "]= " + getProjects(i));
            System.out.println("# name = " + getProjects(i).getName());
         }
      }
      System.out.println("########");
   }

   // **********************************************************************
   // protected
   // **********************************************************************
   // ******** attributes
   protected static PmpContext _Instance = null;

   protected MavenProject rootProject;
   protected ArrayList<PmpProject> _Projects;
   protected int currentProjectIndex;
}
