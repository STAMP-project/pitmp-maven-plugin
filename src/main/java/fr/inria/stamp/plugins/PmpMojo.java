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
   // properties
   // **********************************************************************
   @Parameter(property = "targetModules")
   protected ArrayList<String> targetModules;

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
   public ArrayList<String> getTargetModules()
   {
      return(_TargetModules);
   }

   // **********************************************************************
   public void setTargetModules(ArrayList<String> newClasses)
   {
      _TargetModules = newClasses;
   }

   // **********************************************************************
   public boolean findInTargetModules(String name)
   {
      boolean result = false;

      for (int i = 0; i < getTargetModules().size() && ! result; i++)
      {
         result = getTargetModules().get(i).equals(name);
      }

      return(result);
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
      ArrayList<MavenProject> moduleList = null;
      MavenProject mvnProject;

      // require(getProject() != null)
      // System.out.println("######################## PmpMojo.updateTargetClasses: IN");

      if (targetClasses == null)
      {
         targetClasses = new ArrayList<String>();
      }
      if (targetClasses.isEmpty())
      // we need to get the explicit class list of the current project
      {
         // System.out.println("#### targetClasses: empty");
         classList = PmpContext.getClasses(getProject());
         if (! classList.isEmpty())
         {
            targetClasses.addAll(classList);
         }
      }
      else
      // else just let the target classes specified in the pom.xml

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
   ArrayList<String> _TargetModules;

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
      boolean isTargetModule = (getTargetModules() == null ||
         getTargetModules().isEmpty() ||
         findInTargetModules(getProject().getArtifactId()));

      // System.out.println("#### targetModules(" + getProject().getArtifactId() + "): "
         // + getTargetModules());

      PmpContext.getInstance().updateData(this);
      myPmpProject = new PmpProject(this);
      PmpContext.getInstance().setCurrentProject(myPmpProject);

      // myPmpProject.generateClassToMutateProjects();
      updateTargetClasses();

      pitShouldRun = isTargetModule && super.shouldRun();

      // System.out.println("#### shouldRun(" + getProject().getArtifactId() + "): "
         // + pitShouldRun + " - isTargetModule = " + isTargetModule
         // + " - packaging = " + getProject().getPackaging());

      // System.out.println("################ PmpMojo.shouldRun: OUT");
      return(pitShouldRun);
   }
}
