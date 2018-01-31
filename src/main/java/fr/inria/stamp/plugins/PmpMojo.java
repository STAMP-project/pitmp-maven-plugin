package fr.inria.stamp.plugins;

// **********************************************************************
import java.util.List;
import java.util.Set;
import java.util.ArrayList;
import java.io.File;
import java.util.logging.Logger;

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
import org.pitest.util.Log;

// **********************************************************************
@Mojo(name = "run", defaultPhase = LifecyclePhase.VERIFY,
   requiresDependencyResolution = ResolutionScope.TEST)
public class PmpMojo extends AbstractPitMojo
{
   // **********************************************************************
   // properties
   // **********************************************************************
   @Parameter(property = "targetModules")
   protected ArrayList<String> _TargetModules;

   // if true: do not execute PIT, only display information about shouldRun or not
   @Parameter(defaultValue = "false", property = "shouldDisplayOnly")
   protected boolean _ShouldDisplayOnly;

   // **********************************************************************
   // public
   // **********************************************************************
   // ******** attributes
   public File getBaseDir()
   {
      return(detectBaseDir());
   }

   // **********************************************************************
   public boolean shouldDisplayOnly()
   {
      return(_ShouldDisplayOnly);
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
   }

   // **********************************************************************
   // ******** methods
   public void updateTargetClasses()
   {
      // require(getProject() != null)
      ArrayList<String> completeTargetClasses;
      ArrayList<String> classList;
      ArrayList<MavenProject> moduleList = null;
      MavenProject mvnProject;

      if (targetClasses == null)
      {
         targetClasses = new ArrayList<String>();
      }
      if (targetClasses.isEmpty())
      // we need to get the explicit class list of the current project
      {
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
      for (int i = 0; i < moduleList.size(); i++)
      {
         classList = PmpContext.getClasses(moduleList.get(i));
         if (! classList.isEmpty())
         {
            PmpContext.addNewStrings(targetClasses, classList);
         }
      }
   }

   // **********************************************************************
   // protected
   // **********************************************************************
   // ******** methods
   @Override
   protected Option<CombinedStatistics> analyse()
      throws MojoExecutionException
   {
      Option<CombinedStatistics> result = null;

      result = Option.some(PmpContext.getInstance().getCurrentProject()
         .execute());

      return(result);
   }

   // **********************************************************************
   // called at the beginning of AbstractPitMojo.execute
   @Override
   protected boolean shouldRun()
   {
      boolean pitShouldRun;
      boolean result = false;
      PmpProject myPmpProject;
      // if no targetModules are specified, take all modules
      boolean isTargetModule = (getTargetModules() == null ||
         getTargetModules().isEmpty() ||
         findInTargetModules(getProject().getArtifactId()));

      PmpContext.getInstance().updateData(this);

      myPmpProject = new PmpProject(this);
      PmpContext.getInstance().setCurrentProject(myPmpProject);

      updateTargetClasses();

      if (getProject().getPackaging().equals("pom") &&
          myPmpProject.hasTestCompileSourceRoots() &&
          myPmpProject.hasCompileSourceRoots())
      // force packaging to not pom before calling super.shouldRun
      {
         getProject().setPackaging("jar");
      }
      pitShouldRun = super.shouldRun();
      result = isTargetModule && pitShouldRun;

      // displaying info about why we skip the project
      if (isTargetModule)
      {
         if (getProject().getPackaging().equals("pom"))
         {
            Log.getLogger().info("Project packaging is 'pom'");
         }
         else
         {
            if (! myPmpProject.hasTestCompileSourceRoots())
            {
               Log.getLogger().info("Project has no test");
            }
            if (! myPmpProject.hasCompileSourceRoots())
            {
               Log.getLogger().info("Project has no class to mutate");
            }
         }
      }
      else
      {
         Log.getLogger().info("Project is not a target module");
      }

      if (result && shouldDisplayOnly())
      {
         Log.getLogger().info("Can apply PIT on " + getProject().getArtifactId());
      }
      result = result && (! shouldDisplayOnly());

      return(result);
   }
}
