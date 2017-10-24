package fr.inria.stamp.plugins;

// **********************************************************************
import java.util.List;
import java.util.ArrayList;

import org.apache.maven.project.MavenProject;

// **********************************************************************
public class DescartesContext
{
   // **********************************************************************
   // public
   // **********************************************************************
   // ******** assoications
   public static DescartesContext getInstance()
   {
      if (instance == null)
      {
         instance = new DescartesContext();
      }
      return(instance);
   }

   // **********************************************************************
   public MavenProject getRootProject()
   {
      return(rootProject);
   }

   // **********************************************************************
   public MavenProject getCurrentMvnProject()
   {
      return(currentMvnProject);
   }

   // **********************************************************************
   public DescartesProject getCurrentProject()
   {
      DescartesProject theProject = null;

      if (currentProjectIndex >= 0 && currentProjectIndex < cardProjects())
      {
         theProject = dProjects.get(currentProjectIndex);
      }

      return(theProject);
   }

   // **********************************************************************
   public int cardProjects()
   {
      return(dProjects.size());
   }

   // **********************************************************************
   public DescartesProject getProjects(int index)
   {
      DescartesProject theProject = null;

      if (index >= 0 && index < cardProjects())
      {
         theProject = dProjects.get(index);
      }

      return(theProject);
   }

   // **********************************************************************
   public void appendProjects(DescartesProject aProject)
   {
      System.out.println("######## appendProjects: " + aProject);
      System.out.println("# name = " + aProject.getName() + " - card = " +
         cardProjects());
      // printInfo(true);
      System.out.println("#");

      dProjects.add(aProject);
      currentProjectIndex = cardProjects() - 1;

      System.out.println("# addedProject: " + dProjects.get(cardProjects() - 1));
      System.out.println("# currentProject: " + getCurrentProject());
      System.out.println("# name = " + getCurrentProject().getName() + " - card = " +
         cardProjects());
      // printInfo(true);
   }

   // **********************************************************************
   public DescartesProject findInProjects(String aName)
   {
      DescartesProject theProject = null;
      String id;

      System.out.println("# findInProjects: " + aName + " - card = " + cardProjects());
      for (int i = 0; (i < cardProjects() && theProject == null); i++)
      {
         id = getProjects(i).getMavenProject().getArtifactId();
         System.out.println("# i: " + i + " - Id = " + id + " - dProject: " +
            getProjects(i) + " - mvnProject: " + getProjects(i).getMavenProject());
         if (id.equals(aName))
         {
            theProject = getProjects(i);
         }
      }

      return(theProject);
   }

   // **********************************************************************
   // ******** methods
   public DescartesContext()
   {
      dProjects = new ArrayList<DescartesProject>();
   }

   // **********************************************************************
   public void updateData(MavenProject currentProject)
   {
      if (rootProject == null)
      {
         if (currentProject.getParent() != null)
         // no parents means non multi-module project
         {
            rootProject = currentProject.getParent();
         }
      }
      currentMvnProject = currentProject;
   }

   // **********************************************************************
   public void printInfo(boolean printAll)
   {
      System.out.println("######## Context: cardProjects = " + cardProjects());
      System.out.println("# currentProjectIndex = " + currentProjectIndex);

      if (getCurrentProject() != null)
      {
         System.out.println("# current project: " +
            getCurrentProject().getMavenProject().getArtifactId());
      }

      if (printAll)
      {
         for (int i = 0; i < cardProjects(); i++)
         {
            System.out.println("# project[" + i + "]= " + getProjects(i));
            System.out.println("# name = " + getProjects(i).getName());
            System.out.println("# name = " + dProjects.get(i).getName());
         }
      }
      System.out.println("########");
   }

   // **********************************************************************
   // private
   // **********************************************************************
   // ******** attributes
   private static DescartesContext instance;

   private MavenProject rootProject = null;
   private MavenProject currentMvnProject = null;
   private ArrayList<DescartesProject> dProjects = null;
   private int currentProjectIndex = -1;
}
