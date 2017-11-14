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
// import org.pitest.mutationtest.config.ReportOptions;
import org.pitest.mutationtest.tooling.CombinedStatistics;

import org.pitest.maven.AbstractPitMojo;
import org.pitest.maven.GoalStrategy;
import org.pitest.maven.DependencyFilter;
import org.pitest.maven.NonEmptyProjectCheck;
// import org.pitest.maven.MojoToReportOptionsConverter;
// import org.pitest.maven.SurefireConfigConverter;

// **********************************************************************
@Mojo(name = "run", defaultPhase = LifecyclePhase.VERIFY,
   requiresDependencyResolution = ResolutionScope.TEST)
public class DescartesRunMojo extends AbstractPitMojo
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
   public Boolean isRegularMojo()
   {
      return(_IsRegularMojo);
   }

   // ***********
   public void setIsRegularMojo(Boolean value)
   {
      _IsRegularMojo = value;
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
   public ArrayList<String> getModifiedTargetClasses()
   {
       return(_ModifiedTargetClasses);
   }

   // **********
   public void setModifiedTargetClasses(ArrayList<String> classes)
   {
       _ModifiedTargetClasses = classes;
   }

   // **********************************************************************
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
      List<String> results = null;

      if (isRegularMojo())
      {
         results = targetClasses;
      }
      else
      {
         results = _ModifiedTargetClasses;
      }

      return(results);
   }

   // **********************************************************************
   // ******** methods
   public DescartesRunMojo()
   {
      super();
      _IsRegularMojo = true;
      _RegularTargetClasses = targetClasses;
      _ModifiedTargetClasses = targetClasses;
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

      // final ReportOptions data = new MojoToReportOptionsConverter(this,
      //   new SurefireConfigConverter(), filter).convert();

      DescartesContext.getInstance().updateData(this);

      // result = Option.some(this.goalStrategy.execute(detectBaseDir(), data,
      // result = Option.some(this.goalStrategy.execute(detectBaseDir(),
      //    this.plugins, this.getEnvironmentVariables()));
      // result = Option.some(this.goalStrategy.execute());

      DescartesContext.getInstance().appendProjects(new DescartesProject
         (DescartesContext.getInstance().getCurrentMojo()));

      result = Option.some(DescartesContext.getInstance().getCurrentProject()
         .execute());

      // restore mojo initial (regular) data for next runs
      setModifiedTargetClasses(getRegularTargetClasses());

      return(result);
   }

   // **********************************************************************
   // private
   // **********************************************************************
   // ******** attributes
   Boolean _IsRegularMojo;
   ArrayList<String> _RegularTargetClasses;
   ArrayList<String> _ModifiedTargetClasses;
}
