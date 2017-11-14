package fr.inria.stamp.plugins;

// **********************************************************************
import java.util.List;
import java.util.ArrayList;

import org.apache.maven.project.MavenProject;

import org.pitest.maven.AbstractPitMojo;

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
   public AbstractPitMojo getCurrentMojo()
   {
      return(currentMojo);
   }

   // **********************************************************************
   public DescartesProject getCurrentProject()
   {
      DescartesProject theProject = null;

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
   public DescartesProject getProjects(int index)
   {
      DescartesProject theProject = null;

      if (index >= 0 && index < cardProjects())
      {
         theProject = _Projects.get(index);
      }

      return(theProject);
   }

   // ***********
   public void appendProjects(DescartesProject aProject)
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
   public DescartesProject findInProjects(String aName)
   {
      DescartesProject theProject = null;
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
   public DescartesContext()
   {
      _Projects = new ArrayList<DescartesProject>();
      int currentProjectIndex = -1;
   }

   // **********************************************************************
   public void updateData(AbstractPitMojo mojo)
   {
      if (rootProject == null)
      {
         if (mojo.getProject().getParent() != null)
         // no parents means non multi-module project
         {
            rootProject = mojo.getProject().getParent();
         }
      }
      currentMojo = mojo;
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
   // private
   // **********************************************************************
   // ******** attributes
   private static DescartesContext instance;

   private AbstractPitMojo currentMojo;
   private MavenProject rootProject;
   private ArrayList<DescartesProject> _Projects;
   private int currentProjectIndex;
}
