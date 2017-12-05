package fr.inria.stamp.plugins;

// **********************************************************************
import java.util.List;
import java.util.Set;
import java.util.ArrayList;
import java.io.File;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.project.MavenProject;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.plugins.annotations.LifecyclePhase;

import org.pitest.functional.Option;
import org.pitest.functional.predicate.Predicate;
import org.pitest.mutationtest.config.PluginServices;
import org.pitest.mutationtest.tooling.CombinedStatistics;
import org.pitest.maven.MojoToReportOptionsConverter;
import org.pitest.maven.SurefireConfigConverter;
import org.pitest.maven.AbstractPitMojo;
import org.pitest.maven.RunPitStrategy;
import org.pitest.maven.DependencyFilter;

// **********************************************************************
@Mojo(name = "run", defaultPhase = LifecyclePhase.VERIFY,
   requiresDependencyResolution = ResolutionScope.TEST)
public class PmpMojo extends AbstractPitMojo
{
   // **********************************************************************
   // public
   // **********************************************************************
   // ******** attributes
   public File getBaseDir()
   {
      return(detectBaseDir());
   }

   // **********************************************************************
   // ******** associations
   public Predicate<Artifact> getFilter()
   {
      return(filter);
   }

   // **********************************************************************
   public PluginServices getPlugins()
   {
      return(plugins);
   }

   // **********************************************************************
   public ArrayList<String> getLocalTargetClasses()
   {
      return(_LocalTargetClasses);
   }

   // **********************************************************************
   public void setLocalTargetClasses(ArrayList<String> newClasses)
   {
      _LocalTargetClasses = newClasses;
   }

   // **********************************************************************
   // ******** methods
   public PmpMojo()
   {
      super(new RunPitStrategy(),
        new DependencyFilter(new PluginServices(AbstractPitMojo.class.getClassLoader())),
        new PluginServices(AbstractPitMojo.class.getClassLoader()),
        new PmpNonEmptyProjectCheck());

      // System.out.println("################################ PmpMojo: IN");
      _LocalTargetClasses = new ArrayList<String>();

      // System.out.println("# targetClasses: " + targetClasses);
      // System.out.println("# getProject(): " + getProject());
      // System.out.println("################################ PmpMojo: OUT");
   }

   // **********************************************************************
   // ******** methods
   public void updateTargetClasses()
   {
      ArrayList<String> completeTargetClasses;
      ArrayList<String> classList;
      ArrayList<MavenProject> moduleList;
      MavenProject mvnProject;

      // require(getProject() != null)
      // System.out.println("######################## PmpMojo.updateTargetClasses: IN");

      if (targetClasses == null || targetClasses.isEmpty())
      // we need to get the explicit class list of the current project
      {
         // System.out.println("#### targetClasses: empty");
         classList = PmpContext.getClasses(getProject());
         if (! classList.isEmpty())
         {
            getLocalTargetClasses().addAll(classList);
         }
      }
      else
      // else just let the target classes specified in the pom.xml
      {
         getLocalTargetClasses().addAll(targetClasses);
      }
      // initialize the targetClasses with the classes of the current module
      targetClasses = new ArrayList(getLocalTargetClasses());

      // complete the target classes with other (dependencies) modules classes
      // and add target classes of all getArtifacts which are a project module
      moduleList = PmpContext.getInstance().getDependingModules(getProject());
      PmpContext.printDependModules(getProject(), moduleList);
      for (int i = 0; i < moduleList.size(); i++)
      {
         classList = PmpContext.getClasses(moduleList.get(i));
         if (! classList.isEmpty())
         {
            PmpContext.addNewStrings(targetClasses, classList);
         }
      }

      // System.out.println("#### updated targetClasses: " + targetClasses);

      // System.out.println("######################## PmpMojo.updateTargetClasses: OUT");
   }

   // **********************************************************************
   // protected
   // **********************************************************************
   // ******** attributes
   ArrayList<String> _LocalTargetClasses;

   // ******** methods
   @Override
   protected Option<CombinedStatistics> analyse()
      throws MojoExecutionException
   {
      Option<CombinedStatistics> result;

      // System.out.println("################################ PmpMojo.analyse: IN");

      result = Option.some(PmpContext.getInstance().getCurrentProject()
         .execute());

      // System.out.println("################################ PmpMojo.analyse: OUT");
      return(result);
   }

   // **********************************************************************
   // called at the beginning of AbstractPitMojo.execute
   @Override
   protected boolean shouldRun()
   {
      // System.out.println("################ PmpMojo.shouldRun: IN");

      boolean pitShouldRun;
      PmpProject myPmpProject;

      PmpContext.getInstance().updateData(this);
      myPmpProject = new PmpProject(this);
      PmpContext.getInstance().setCurrentProject(myPmpProject);

      // myPmpProject.generateClassToMutateProjects();
      updateTargetClasses();

      pitShouldRun = super.shouldRun();

      System.out.println("#### shouldRun: " + pitShouldRun + " - getProject() = " +
         getProject().getArtifactId() + " - packaging = " + getProject().getPackaging());

      // System.out.println("################ PmpMojo.shouldRun: OUT");
      return(pitShouldRun);
   }
}
