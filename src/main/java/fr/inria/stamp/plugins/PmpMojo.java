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

import org.pitest.maven.AbstractPitMojo;

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
   // original targetClasses from the pom.xml
   // if empty, replaced with the explicit class list of the module
   public ArrayList<String> getRegularTargetClasses()
   {
       return(_RegularTargetClasses);
   }

   // **********
   public void setRegularTargetClasses(ArrayList<String> classes)
   {
       _RegularTargetClasses = classes;
   }

   // **********************************************************************
   @Override
   public List<String> getTargetClasses()
   {
      if (getProject() != null)
      {
         updateTargetClasses();
      }
      return(targetClasses);
   }

   // **********************************************************************
   // ******** methods
   public PmpMojo()
   {
      super();
      System.out.println("################################ PmpMojo: IN");

      _OriginalTargetClasses = targetClasses;
      _RegularTargetClasses = targetClasses;

      System.out.println("# targetClasses: " + targetClasses);
      System.out.println("# OriginalTargetClasses: " + _OriginalTargetClasses);
      System.out.println("# RegularTargetClasses: " + getRegularTargetClasses());
      System.out.println("################################ PmpMojo: OUT");
   }

   // **********************************************************************
   // ******** methods
   public void updateTargetClasses()
   {
      // require(getProject() != null)
      System.out.println("################################ PmpMojo.updateTargetClasses: IN");

      if (targetClasses == null || targetClasses.isEmpty())
      // we need to set the explicit package filter
      {
         System.out.println("# targetClasses: empty");
         String outputDirName = getProject().getBuild().getOutputDirectory();
         File outputDir = new File(outputDirName);
         if (outputDir.exists())
         {
            System.out.println("# outputDir.exists");
            DirectoryClassPathRoot classRoot = new DirectoryClassPathRoot(outputDir);
            targetClasses = new ArrayList<String>(classRoot.classNames());
            // setRegularTargetClasses(new ArrayList<String>(targetClasses));
            setRegularTargetClasses(targetClasses);
         }
      }
      // else just let the target classes specified in the pom.xml
      {
         System.out.println("# targetClasses: " + targetClasses);
         setRegularTargetClasses(targetClasses);
         System.out.println("# RegularTargetClasses: " + getRegularTargetClasses());
      }

      // <cael>: to do: add ExcludedClasses and ExcludedMethods
      System.out.println("################################ PmpMojo.updateTargetClasses: OUT");
   }

   // **********************************************************************
   public void replaceTargetClasses(ArrayList<String> classes)
   {
      targetClasses = classes;
   }

   // **********************************************************************
   // protected
   // **********************************************************************
   // ******** methods
   @Override
   protected Option<CombinedStatistics> analyse()
      throws MojoExecutionException
   {
      Option<CombinedStatistics> result;

      PmpContext.getInstance().updateData(this);
      PmpContext.getInstance().appendProjects(new PmpProject(this));

      updateTargetClasses();

      result = Option.some(PmpContext.getInstance().getCurrentProject()
         .execute());

      return(result);
   }

   // **********************************************************************
   // private
   // **********************************************************************
   // ******** attributes
   protected List<String> _OriginalTargetClasses;
   protected ArrayList<String> _RegularTargetClasses;
}
