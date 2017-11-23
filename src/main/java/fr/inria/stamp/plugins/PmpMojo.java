package fr.inria.stamp.plugins;

// **********************************************************************
import java.util.List;
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
import org.pitest.classpath.DirectoryClassPathRoot;
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
   @Override
   public List<String> getExcludedClasses()
   {
      return(PmpContext.getInstance().getCurrentProject().getExcludedClasses());
   }

   // **********************************************************************
   public List<String> getLocalExcludedClasses()
   {
      return(super.getExcludedClasses());
   }

   // **********************************************************************
   @Override
   public List<String> getExcludedMethods()
   {
      return(PmpContext.getInstance().getCurrentProject().getExcludedMethods());
   }

   // **********************************************************************
   public List<String> getLocalExcludedMethods()
   {
      return(super.getExcludedMethods());
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
      PmpProject currentProject = PmpContext.getInstance().getCurrentProject();
      ArrayList<String> completeTargetClasses;

      // require(getProject() != null)
      // System.out.println("######################## PmpMojo.updateTargetClasses: IN");

      if (targetClasses == null || targetClasses.isEmpty())
      // we need to get the explicit class list of the current project
      {
         // System.out.println("#### targetClasses: empty");
         String outputDirName = getProject().getBuild().getOutputDirectory();
         File outputDir = new File(outputDirName);
         // <cael>: check id this could happen, and what does it mean, and decide what
         // to do: value of targetClass or throw exception
         if (outputDir.exists())
         {
            System.out.println("#### outputDir.exists");
            DirectoryClassPathRoot classRoot = new DirectoryClassPathRoot(outputDir);
            getLocalTargetClasses().addAll(classRoot.classNames());
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
      for (int i = 0; i < currentProject.cardClassToMutateProjects(); i++)
      {
         PmpContext.addNewStrings(targetClasses, currentProject
               .getClassToMutateProjects(i).getTheMojo().getTargetClasses());
      }

      System.out.println("# updated targetClasses: " + targetClasses);

      // <cael>: to do: add ExcludedClasses and ExcludedMethods
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

      System.out.println("################################ PmpMojo.analyse: IN");

      result = Option.some(PmpContext.getInstance().getCurrentProject()
         .execute());

      System.out.println("################################ PmpMojo.analyse: OUT");
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
      PmpContext.getInstance().appendProjects(myPmpProject);

      // PmpContext.getInstance().getCurrentProject().generateClassToMutateProjects();
      myPmpProject.generateClassToMutateProjects();
      updateTargetClasses();

      pitShouldRun = super.shouldRun();

      // create the ReportOptions as PiTest does and save it for futur use
      // do this here to ensure getRegularPitOprions() != null, even if pitest
      // doesn't run on it
      PmpContext.getInstance().getCurrentProject().setRegularPitOptions
         (new MojoToReportOptionsConverter(this, new SurefireConfigConverter(),
             getFilter())
         .convert());

      System.out.print("# getProject() = " + getProject().getArtifactId());
      System.out.println(" - shouldRun: pitShouldRun = " + pitShouldRun);
      // System.out.println("################ PmpMojo.shouldRun: OUT");

      return(pitShouldRun);
   }
}